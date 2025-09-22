package com.tibafit.service.cart; // 注意 package 路徑

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tibafit.dto.cart.LinePayRequest;
import com.tibafit.dto.cart.LinePayResponse;
import com.tibafit.model.cart.OrderItemVO;
import com.tibafit.model.cart.OrdersVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class LinePayService {

    @Value("${line.pay.channel-id}")
    private String channelId;
    @Value("${line.pay.channel-secret}")
    private String channelSecret;
    @Value("${line.pay.api.url}")
    private String linePayApiUrl;

    // ★★★ 修正點 1：注入 OrdersService 和 RestTemplate ★★★
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OrdersService ordersService;

    @Autowired
    public LinePayService(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    // ★★★ 修正點 2：方法的參數改為接收 Integer orderId ★★★
    public String requestPayment(Integer orderId) throws Exception {

        // ★★★ 修正點 3：用 orderId 從資料庫撈出訂單資料 ★★★
        OrdersVO yourOrder = ordersService.get(orderId);
        if (yourOrder.getPaymentStatus() != 0) { // 假設 0 是未付款
            throw new IllegalStateException("此訂單 " + orderId + " 的狀態無法發起付款");
        }

        // 1. 建立我們的請求 Body
        LinePayRequest requestBody = createRequestBody(yourOrder);
        String requestBodyJson = objectMapper.writeValueAsString(requestBody);

        // 2. 準備 Headers 和簽章
        String nonce = UUID.randomUUID().toString();
        String requestUri = "/v3/payments/request";
        String signature = calculateSignature(channelSecret, requestUri, requestBodyJson, nonce);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-LINE-ChannelId", channelId);
        headers.set("X-LINE-Authorization-Nonce", nonce);
        headers.set("X-LINE-Authorization", signature);

        // 3. 發送請求
        HttpEntity<String> entity = new HttpEntity<>(requestBodyJson, headers);
        String responseJson = restTemplate.postForObject(linePayApiUrl + requestUri, entity, String.class);

        // 4. 解析回應並回傳付款網址
        LinePayResponse response = objectMapper.readValue(responseJson, LinePayResponse.class);
        if ("0000".equals(response.getReturnCode()) && response.getInfo() != null && response.getInfo().getPaymentUrl() != null) {
            return response.getInfo().getPaymentUrl().getWeb(); // ★★★ 修正點 4：取得正確的 web 網址 ★★★
        } else {
            throw new RuntimeException("LINE Pay 請求失敗: " + response.getReturnMessage());
        }
    }

    // createRequestBody 和 calculateSignature 方法保持不變 (我上次已提供)
    private LinePayRequest createRequestBody(OrdersVO yourOrder) {
        List<LinePayRequest.Product> products = new ArrayList<>();
        for (OrderItemVO item : yourOrder.getOrderItems()) {
            LinePayRequest.Product product = new LinePayRequest.Product();
            product.setName(item.getProduct().getProductName());
            product.setQuantity(item.getOrderItemQuantity());
            product.setPrice(item.getBuyPrice());
            products.add(product);
        }
        // ★★★ 修正點 1：如果有點數折抵，把它當成一個「負數商品」加進去 ★★★
        Integer usedPoints = yourOrder.getUsedPointsAmount();
        if (usedPoints != null && usedPoints > 0) {
            LinePayRequest.Product discountProduct = new LinePayRequest.Product();
            discountProduct.setName("網站點數折抵");
            discountProduct.setQuantity(1);
            discountProduct.setPrice(-usedPoints); // 價格是負的折抵金額
            products.add(discountProduct);
        }
        // 2. 計算使用者實際要付的錢 (折扣後)
        int finalAmount = yourOrder.getTotalPrice() - (yourOrder.getUsedPointsAmount() != null ? yourOrder.getUsedPointsAmount() : 0);
        LinePayRequest.Package pkg = new LinePayRequest.Package();
        pkg.setId("package-" + yourOrder.getOrderId());
        // ★★★ 修正點：Package 的 amount 必須是內部所有 product 的加總 (折扣前) ★★★
        // 因為現在 products 列表的總和 (商品總額 - 點數) 已經等於 finalAmount 了
        pkg.setAmount(finalAmount);

        pkg.setProducts(products);

        LinePayRequest.RedirectUrls urls = new LinePayRequest.RedirectUrls();
        urls.setConfirmUrl("http://localhost:8080/frontend-template/pay_success.html");
        urls.setCancelUrl("http://localhost:8080/frontend-template/pay_fail.html");
        LinePayRequest request = new LinePayRequest();
        request.setAmount(finalAmount);
        request.setCurrency("TWD");
        request.setOrderId(yourOrder.getOrderCode());
        request.setPackages(Collections.singletonList(pkg));
        request.setRedirectUrls(urls);
        return request;
    }

    private String calculateSignature(String channelSecret, String uri, String requestBody, String nonce) throws Exception {
        String message = channelSecret + uri + requestBody + nonce;
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(channelSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hmacBytes = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hmacBytes);
    }
}