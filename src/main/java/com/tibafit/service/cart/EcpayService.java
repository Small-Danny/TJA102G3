package com.tibafit.service.cart;

import com.tibafit.dto.cart.EcpayRequest;
import com.tibafit.model.cart.OrdersVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EcpayService {

    @Value("${ecpay.merchantId}")
    private String merchantId;

    @Value("${ecpay.hashKey}")
    private String hashKey;

    @Value("${ecpay.hashIv}")
    private String hashIv;

    @Value("${ecpay.returnUrl}")
    private String returnUrl;

    /**
     * ★★★ 我們現在換回動態版本，因為加密邏輯已經正確了 ★★★
     */
    public EcpayRequest createOrder(OrdersVO newOrder) {
        EcpayRequest request = new EcpayRequest();

        request.setMerchantID(this.merchantId);
        request.setReturnURL(this.returnUrl); // 注意：測試時 localhost 可行，正式上線需為公網網址
        request.setMerchantTradeNo(newOrder.getOrderCode());

        // 計算最終應付金額
        int totalPrice = newOrder.getTotalPrice();
        int usedPoints = (newOrder.getUsedPointsAmount() != null) ? newOrder.getUsedPointsAmount() : 0;
        int finalAmount = totalPrice - usedPoints;
        request.setTotalAmount(String.valueOf(finalAmount));

        request.setMerchantTradeDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
        request.setTradeDesc("TibaFit 健身商品訂單");
        request.setItemName("商品一批");

        // 固定的必填參數
        request.setChoosePayment("Credit");
        request.setPaymentType("aio");
        request.setEncryptType("1");

        try {
            String checkMacValue = generateCheckMacValue(request.toMap());
            request.setCheckMacValue(checkMacValue);
        } catch (Exception e) {
            throw new RuntimeException("產生 CheckMacValue 失敗", e);
        }

        return request;
    }

    /**
     * 產生 CheckMacValue (根據您朋友成功範例的最終正確版本)
     */
    private String generateCheckMacValue(Map<String, String> rawParams) throws Exception {
        // (A) 排除 CheckMacValue 並依 Key 排序
        Map<String, String> sortedParams = rawParams.entrySet().stream()
                .filter(e -> !"CheckMacValue".equalsIgnoreCase(e.getKey()))
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (o, n) -> o, LinkedHashMap::new));

        // (B) 串接成 HashKey=...&key=value&...&HashIV=...
        StringBuilder sb = new StringBuilder();
        sb.append("HashKey=").append(this.hashKey);
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            sb.append('&').append(entry.getKey()).append('=').append(entry.getValue());
        }
        sb.append("&HashIV=").append(this.hashIv);

        // (C) 執行 ECPay 特殊規則的 URL Encode 並轉小寫
        String encodedString = ecpayUrlEncode(sb.toString());

        // (D) SHA-256 加密後轉大寫
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(encodedString.getBytes(StandardCharsets.UTF_8));
        return bytesToHexUpper(digest);
    }

    /**
     * ECPay 規則的 URL Encode：URLEncoder + toLowerCase + 特定字元還原
     * ★★★ 關鍵：空白會被轉成 '+'，這點是正確的，不需要換成 %20 ★★★
     */
    private String ecpayUrlEncode(String s) {
        try {
            String encoded = URLEncoder.encode(s, StandardCharsets.UTF_8.toString()).toLowerCase();
            return encoded.replace("%21", "!")
                    .replace("%28", "(")
                    .replace("%29", ")")
                    .replace("%2a", "*")
                    .replace("%7e", "~");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 將 byte 陣列轉換成大寫的 16 進位字串
     */
    private String bytesToHexUpper(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}