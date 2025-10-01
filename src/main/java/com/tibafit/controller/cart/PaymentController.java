package com.tibafit.controller.cart;

import com.tibafit.dto.cart.EcpayRequest;
import com.tibafit.dto.cart.LinePayRequestDTO;
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
import org.springframework.transaction.annotation.Transactional;
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

// 確保 CheckoutService 有 @Autowired
	@PostMapping("/api/line-pay/confirm")
	@ResponseBody
	@Transactional
	public ResponseEntity<String> handleLinePayConfirm(@RequestBody Map<String, String> request) {
		String orderCode = request.get("orderId"); // <-- 將變數名稱改為 orderCode
		String transactionId = request.get("transactionId");

		if (orderCode == null || transactionId == null) {
			System.err.println("LINE Pay 確認請求缺少必要的交易資訊");
			return ResponseEntity.badRequest().body("{\"message\":\"交易資訊不完整\"}");
		}

		try {
			// ★★★ 修正點：使用 markPaidByOrderCode() 方法 ★★★
			OrdersVO updatedOrder = checkoutService.markPaidByOrderCode(orderCode); // 改用 orderCode
			// 清空購物車
			cartService.clear(updatedOrder.getUserId());

			System.out.println("訂單 " + orderCode + " 狀態更新成功，購物車已清空！");
			return ResponseEntity.ok("{\"message\":\"訂單已成功更新\"}");
		} catch (Exception e) {
			// 注意：這裡的 orderId 仍是字串，但變數名已經修正為 orderCode
			System.err.println("更新訂單 " + orderCode + " 狀態時發生錯誤: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\":\"處理訂單時發生錯誤\"}");
		}
	}

	/**
	 * ★★★ B 做法的核心 ★★★ 接收前端帶有 orderId 的請求，由後端產生完整的 ECPay 表單頁面
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

			// ★ 新增：傳給模板，讓綠界成功頁出現「返回商店」按鈕
			model.addAttribute("ClientBackURL", ecpayRequest.getClientBackURL());

			// 4. 回傳 Thymeleaf 模板的路徑
			// 這會去渲染 `resources/templates/frontend/pages/ecpay-form.html` 這個檔案
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
		// System.out.println("!!! CheckMacValue 驗證失敗 !!!");
		// return "0|Error";
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

	/**
	 * 處理 LINE Pay 支付完成後的瀏覽器回呼 (GET) LINE Pay 支付完成後，會將使用者導向回這個 URL。
	 */
	@GetMapping("/api/line-pay/callback")
	public String handleLinePayCallback(@RequestParam Map<String, String> callbackData, Model model) {
		String orderCode = callbackData.get("orderId"); // <-- 將變數名稱改為 orderCode
		String transactionId = callbackData.get("transactionId");

		if (orderCode == null || transactionId == null) {
			model.addAttribute("message", "LINE Pay 回呼缺少必要的交易資訊");
			return "frontend/pages/pay_fail";
		}

		try {
			// ★★★ 修正點：使用 markPaidByOrderCode() 來更新訂單狀態 ★★★
			OrdersVO updatedOrder = checkoutService.markPaidByOrderCode(orderCode); // 直接傳入 orderCode

			// 清空購物車
			cartService.clear(updatedOrder.getUserId());

			model.addAttribute("orderCode", updatedOrder.getOrderCode());
			model.addAttribute("transactionId", transactionId);
			return "frontend/pages/pay_success";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", "處理 LINE Pay 回呼時發生錯誤：" + e.getMessage());
			return "frontend/pages/pay_fail";
		}
	}

	/**
	 * 處理 LINE Pay 支付完成後的後端非同步通知 (POST) 這是 LINE Pay 伺服器發出的，用於確保交易狀態的最終一致性。
	 */
	@PostMapping("/api/line-pay/notification")
	@ResponseBody
	public ResponseEntity<String> handleLinePayNotification(@RequestBody Map<String, Object> notificationData) {
		System.out.println("收到 LINE Pay 非同步通知:");
		notificationData.forEach((key, value) -> System.out.println(key + " = " + value));

		// 從通知中解析訂單資訊
		// 注意：這裡的資料結構可能與你的 DTO 不完全相同，需要根據 LINE Pay 文件調整
		String orderId = (String) notificationData.get("orderId");
		String transactionId = (String) notificationData.get("transactionId");
		String returnCode = (String) notificationData.get("returnCode");

		if ("0000".equals(returnCode)) {
			try {
				// 呼叫 CheckoutService 根據訂單編號來標記已付款
				OrdersVO updatedOrder = checkoutService.markPaidByOrderCode(orderId);

				// 清空購物車
				cartService.clear(updatedOrder.getUserId());

				return ResponseEntity.ok("success"); // 回覆 success 給 LINE Pay
			} catch (Exception e) {
				System.err.println("處理 LINE Pay 非同步通知時發生錯誤: " + e.getMessage());
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error");
			}
		} else {
			// 處理付款失敗的通知
			System.out.println("訂單 " + orderId + " 付款失敗，代碼: " + returnCode);
			return ResponseEntity.ok("success");
		}
	}
}