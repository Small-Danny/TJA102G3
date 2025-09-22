package com.tibafit.controller.cart;

import com.tibafit.dto.cart.EcpayRequest;
import com.tibafit.dto.cart.LinePayRequestDTO;
import com.tibafit.dto.cart.OrdersDTO;
import com.tibafit.model.cart.OrdersVO;
import com.tibafit.repository.cart.OrdersDAO;
import com.tibafit.service.cart.CartService;
import com.tibafit.service.cart.CheckoutService;
import com.tibafit.service.cart.EcpayService;
import com.tibafit.service.cart.LinePayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// 由於此控制器同時處理 REST API 和頁面，使用 @Controller
@Controller
public class PaymentController {

    @Autowired
    private LinePayService linePayService;

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private CartService cartService;

    @Autowired
    private EcpayService ecpayService;

    @Autowired
    private OrdersDAO ordersDAO;

    /**
     * 前端呼叫此 API 以取得 LINE Pay 付款連結 (API)
     */
    @PostMapping("/api/line-pay/request")
    @ResponseBody
    public ResponseEntity<String> requestLinePayPayment(@RequestBody LinePayRequestDTO request) {
        try {
            String paymentUrl = linePayService.requestPayment(request.getOrderId());
            return ResponseEntity.ok("{\"paymentUrl\":\"" + paymentUrl + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"建立 LINE Pay 交易失敗: " + e.getMessage() + "\"}");
        }
    }

    /**
     * 模擬 LINE Pay 付款成功 (API)
     */
    @PostMapping("/api/line-pay/mock-confirm")
    @ResponseBody
    public ResponseEntity<OrdersDTO> mockLinePayConfirm(@RequestBody LinePayRequestDTO request) {
        try {
            Integer orderId = request.getOrderId();
            OrdersVO updatedOrder = checkoutService.markPaid(orderId);
            cartService.clear(updatedOrder.getUserId());
            return ResponseEntity.ok(OrdersDTO.from(updatedOrder));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    /**
     * ★★★ B 做法的核心 ★★★
     * 接收前端帶有 orderId 的請求，由後端產生完整的 ECPay 表單頁面
     */
    @GetMapping("/payment/ecpay")
    public String redirectToEcpay(@RequestParam Integer orderId, Model model) {
        try {
            // 1. 根據 orderId 從資料庫查詢訂單
            OrdersVO order = ordersDAO.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("在 redirectToEcpay 中找不到訂單: " + orderId));

            // 2. 呼叫 EcpayService 產生綠界支付需要的參數 (包含 CheckMacValue)
            EcpayRequest ecpayRequest = ecpayService.createOrder(order);

            // 3. 將所有參數一個一個加到 Model 中，以便 Thymeleaf 模板渲染
            model.addAttribute("MerchantID", ecpayRequest.getMerchantID());
            model.addAttribute("MerchantTradeNo", ecpayRequest.getMerchantTradeNo());
            model.addAttribute("MerchantTradeDate", ecpayRequest.getMerchantTradeDate());
            model.addAttribute("PaymentType", ecpayRequest.getPaymentType());
            model.addAttribute("TotalAmount", ecpayRequest.getTotalAmount());
            model.addAttribute("TradeDesc", ecpayRequest.getTradeDesc());
            model.addAttribute("ItemName", ecpayRequest.getItemName());
            model.addAttribute("ReturnURL", ecpayRequest.getReturnURL());
            model.addAttribute("ChoosePayment", ecpayRequest.getChoosePayment());
            model.addAttribute("CheckMacValue", ecpayRequest.getCheckMacValue());
            model.addAttribute("EncryptType", ecpayRequest.getEncryptType());

            // 4. 回傳 Thymeleaf 模板的路徑
            //    這會去渲染 `resources/templates/frontend/pages/ecpay-form.html` 這個檔案
            return "frontend/pages/ecpay-form";

        } catch (Exception e) {
            e.printStackTrace();
            // 可以在這裡導向一個統一的錯誤頁面
            model.addAttribute("errorMessage", "產生綠界支付表單時發生錯誤：" + e.getMessage());
            return "error"; // 假設您有一個 error.html
        }
    }


    // ECPay 非同步通知 (API)
    @PostMapping("/payment/ecpay/return")
    @ResponseBody
    public String handleEcpayCallback(@RequestParam Map<String, String> callbackData) {
        System.out.println("收到綠界 Callback 通知:");
        callbackData.forEach((key, value) -> System.out.println(key + " = " + value));

        // 驗證從綠界回傳的 CheckMacValue (高度建議正式上線時實作)
        // boolean isCheckMacValueValid = ecpayService.validateCallback(callbackData);
        // if (!isCheckMacValueValid) {
        //     System.out.println("!!! CheckMacValue 驗證失敗 !!!");
        //     return "0|Error";
        // }

        String orderCode = callbackData.get("MerchantTradeNo");
        String rtnCode = callbackData.get("RtnCode");

        // ★★★ 核心修正點：實際去更新訂單狀態 ★★★
        if ("1".equals(rtnCode)) {
            System.out.println("訂單 " + orderCode + " 付款成功！準備更新資料庫...");
            try {
                // 透過 checkoutService 將訂單狀態更新為「已付款」
                OrdersVO updatedOrder = checkoutService.markPaidByOrderCode(orderCode);
                // 付款成功後，清空該使用者的購物車
                cartService.clear(updatedOrder.getUserId());
                System.out.println("訂單 " + orderCode + " 狀態更新成功，購物車已清空！");
            } catch (Exception e) {
                System.err.println("更新訂單 " + orderCode + " 狀態時發生錯誤: " + e.getMessage());
                // 即使出錯，還是要回 1|OK，不然綠界會一直重試
            }
        } else {
            System.out.println("訂單 " + orderCode + " 付款失敗，RtnCode: " + rtnCode);
            // 可以在這裡加入訂單失敗的處理邏輯
        }

        return "1|OK";
    }


}