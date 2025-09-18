package com.tibafit.service.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tibafit.model.cart.OrdersVO;
import com.tibafit.repository.cart.OrdersDAO;
import com.tibafit.repository.cart.ProductDAO;

//	成功時把訂單標記為已付款並清空使用者的購物車；
//	失敗時把訂單標記為失敗。

@Service // 金流相關服務（這裡提供「假金流」流程）
public class PaymentService {
	private final CheckoutService checkoutService; // 內部會更新訂單付款狀態（已付/失敗）與付款時間
	private final CartService cartService; // 付款成功後清空購物車（Redis）

	@Autowired // 建構子注入：由 Spring 產生並注入相依的服務
	public PaymentService(CheckoutService checkoutService, CartService cartService) {
		this.checkoutService = checkoutService;
		this.cartService = cartService;
	}

	/**
	 * 模擬支付流程（假金流）
	 * 
	 * @param userId  前端傳入的使用者 ID（⚠ 正式場景建議從 session/訂單取得，而非信任前端）
	 * @param orderId 要變更狀態的訂單 ID
	 * @param success 模擬結果：true=成功 / false=失敗
	 * @return 更新後的訂單
	 *
	 *         流程： 1) 人工延遲 800ms 模擬金流往返時間 2) 成功 → 呼叫
	 *         checkoutService.markPaid(orderId) 並清空該使用者購物車 3) 失敗 → 呼叫
	 *         checkoutService.markFailed(orderId)
	 *
	 *         注意： - Thread.sleep() 會阻塞執行緒；僅適用於本地模擬。正式環境應改用非同步回呼/訊息佇列。 -
	 *         建議流程需具「冪等性」（重複收到同筆支付通知不應造成重複入帳），可在 Service 內檢查舊狀態。 - 清空購物車時最好驗證訂單的
	 *         userId 是否等於傳入的 userId，避免誤清他人購物車。
	 */
	public OrdersVO mockPay(Integer userId, Integer orderId, boolean success) {
		try {
			Thread.sleep(800); // 模擬第三方金流延遲（DEMO 用）
		} catch (Exception ignored) {
		}

		if (success) {
			var o = checkoutService.markPaid(orderId); // 設為已付款並寫入付款時間
			cartService.clear(userId); // 付款成功 → 清空該使用者的購物車（Redis）
			return o;
		}
		return checkoutService.markFailed(orderId); // 付款失敗 → 設為失敗（paymentStatus=2）
	}
}
