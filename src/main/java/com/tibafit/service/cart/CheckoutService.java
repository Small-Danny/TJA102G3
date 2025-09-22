package com.tibafit.service.cart;

import com.tibafit.model.cart.OrderItemVO;
import com.tibafit.model.cart.OrdersVO;
import com.tibafit.repository.cart.OrdersDAO;
import com.tibafit.repository.cart.ProductDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Random;

//	CheckoutService 會把Redis 購物車轉成資料庫中的訂單主檔/明細：確認品項與價格、建立明細與總價、處
//	理使用點數、存檔後清空購物車。另提供 markPaid/markFailed 更新付款狀態的兩個交易性方法。

@Service // 結帳流程服務：把 Redis 購物車轉成資料庫中的訂單（orders + order_item）
public class CheckoutService {
	private final CartService cartservice;
	private final OrdersDAO ordersDAO;
	private final ProductDAO productDAO;

	@Autowired
	public CheckoutService(CartService cartservice, OrdersDAO ordersDAO, ProductDAO productDAO) {
		this.cartservice = cartservice;
		this.ordersDAO = ordersDAO;
		this.productDAO = productDAO;
	}

	/**
	 * 由購物車建立訂單 步驟： 1) 讀 Redis 購物車，無品項則丟錯 2) 建立 OrdersVO 主檔（未付款、未出貨），帶收件資訊與訂單碼 3)
	 * 逐品項查「上架時的單價」組成明細 OrderItemVO，計算總價與點數使用 4) save(orders) 讓 JPA 以 cascade
	 * 一次存主檔與明細 5) 清空 Redis 購物車
	 */
	@Transactional
	public OrdersVO createOrderFromCart(Integer userId, String rn, String rp, String ra, Integer usedPts) {
		var cart = cartservice.getCart(userId);
		if (cart == null || cart.isEmpty())
			throw new IllegalStateException("購物車是空的");

		OrdersVO vo = new OrdersVO();
		vo.setUserId(userId);
		vo.setOrderStatus(0); // 0=新訂單/待處理（依你的狀態碼設計）
		vo.setPaymentStatus(0); // 0=未付款
		vo.setPaymentTime(null); // 未付款不寫時間（配合你已改為可為 null）
		vo.setOrderCode("ORD" + System.currentTimeMillis() + (new Random().nextInt(9000) + 1000)); // 簡單流水；如需保證唯一可用
																									// existsByOrderCode
																									// 重試

		// 收件資訊（非空才覆寫 entity 預設值）
		if (rn != null)
			vo.setRecipientName(rn);
		if (rp != null)
			vo.setRecipientPhone(rp);
		if (ra != null)
			vo.setRecipientAddress(ra);

		int total = 0;
		for (var e : cart.entrySet()) {
			Integer pid = Integer.valueOf(e.getKey().toString());
			Integer qty = (Integer) e.getValue();

			// 只在商品上架時回單價；否則回 null
			Integer price = productDAO.findOnShelfPrice(pid);
			if (price == null)
				throw new IllegalStateException("商品 " + pid + " 不存在或未上架");

			// 建立訂單明細（快照：單價、小計、商品參照）
			OrderItemVO oit = new OrderItemVO();
			oit.setOrder(vo); // 維護雙向關聯（子指向父）
			oit.setProduct(productDAO.getReferenceById(pid)); // 使用 reference 掛外鍵，不會額外查詢 DB
			oit.setOrderItemQuantity(qty);
			oit.setBuyPrice(price);
			oit.setItemTotalPrice(price * qty);
			oit.setOrderItemCode("ITM" + pid + "_" + (new Random().nextInt(900000) + 100000)); // 簡單碼

			vo.getOrderItems().add(oit); // 父持有子（OneToMany），交由 cascade 一起存
			total += oit.getItemTotalPrice();
		}

		// 使用點數：不可超過總價、不可為負（null 視為 0）
		int safeUsed = Math.max(0, Math.min(usedPts == null ? 0 : usedPts, total));
		vo.setUsedPointsAmount(safeUsed);

		vo.setTotalPrice(total);

		// 儲存：有設定 cascade=ALL，所以會一併存入 order_items
		OrdersVO saved = ordersDAO.save(vo);

		// 成功建單後，清空 Redis 購物車
		cartservice.clear(userId);

		return saved;
	}

	/** 標示付款成功（設為已付並寫入付款時間） */
	@Transactional
	public OrdersVO markPaid(Integer id) {
		var o = ordersDAO.findById(id).orElseThrow();
		o.setPaymentStatus(1); // 1=已付款
		o.setPaymentTime(LocalDateTime.now()); // 付款時間
		return o; // 於交易結束時由 JPA flush
	}

	/** 標示付款失敗（僅狀態改為失敗） */
	@Transactional
	public OrdersVO markFailed(Integer id) {
		var o = ordersDAO.findById(id).orElseThrow();
		o.setPaymentStatus(2); // 2=失敗
		// 保留 paymentTime 為 null（或依需求清空）
		return o;
	}

	/**
	 * ★★★ 這就是我們要實作的新方法 ★★★
	 * 根據訂單編號 (orderCode) 將訂單標記為已付款
	 *
	 * @param orderCode 來自綠界 Callback 的訂單編號 (MerchantTradeNo)
	 * @return 更新後的訂單物件
	 */
	@Transactional // 加上 @Transactional 確保資料庫操作的原子性
	public OrdersVO markPaidByOrderCode(String orderCode) {
		// 1. 使用我們剛剛在 DAO 新增的方法，根據 orderCode 找出訂單
		//    如果找不到，就拋出一個例外
		OrdersVO order = ordersDAO.findByOrderCode(orderCode)
				.orElseThrow(() -> new NoSuchElementException("找不到訂單，訂單編號: " + orderCode));

		// 2. 檢查訂單是否已經是「已付款」狀態，避免重複處理
		if (order.getPaymentStatus() != null && order.getPaymentStatus() == 1) {
			System.out.println("訂單 " + orderCode + " 已是付款狀態，無需重複更新。");
			return order;
		}

		// 3. 更新訂單狀態
		order.setPaymentStatus(1); // 1 代表「已付款」
		order.setPaymentTime(LocalDateTime.now()); // 記錄付款時間

		// 4. 將更新後的訂單存回資料庫
		return ordersDAO.save(order);
	}
}
