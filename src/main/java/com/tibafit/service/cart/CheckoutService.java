package com.tibafit.service.cart;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tibafit.model.cart.OrderItemVO;
import com.tibafit.model.cart.OrdersVO;
import com.tibafit.model.cart.ProductVO;
import com.tibafit.repository.cart.OrdersDAO;
import com.tibafit.repository.cart.ProductDAO;

/**
 * CheckoutService 負責： 1. 從 Redis 購物車建立訂單 2. 鎖定庫存（reserved_stock） 3. 更新付款狀態（成功 →
 * 扣庫存，失敗 → 釋放 reserved_stock）
 *
 * 📌 注意： - reserved_stock：下單時鎖定，避免超賣 - stock_quantity：付款成功後才真正扣除
 */
@Service
public class CheckoutService {
	private final CartService cartservice; // 購物車服務（操作 Redis 購物車）
	private final OrdersDAO ordersDAO; // Orders DAO
	private final ProductDAO productDAO; // Product DAO

	@Autowired
	public CheckoutService(CartService cartservice, OrdersDAO ordersDAO, ProductDAO productDAO) {
		this.cartservice = cartservice;
		this.ordersDAO = ordersDAO;
		this.productDAO = productDAO;
	}

	/**
	 * 建立訂單流程： 1. 從 Redis 取購物車 2. 建立 OrdersVO 主檔（未付款狀態） 3. 檢查庫存，鎖定 reserved_stock 4.
	 * 建立訂單明細（OrderItemVO） 5. 儲存訂單（含明細） 6. 清空 Redis 購物車
	 */
	@Transactional
	public OrdersVO createOrderFromCart(Integer userId, String rn, String rp, String ra, Integer usedPts) {
		var cart = cartservice.getCart(userId);
		if (cart == null || cart.isEmpty())
			throw new IllegalStateException("購物車是空的");

		OrdersVO vo = new OrdersVO();
		vo.setUserId(userId);
		vo.setOrderStatus(0); // 0 = 新訂單/待處理
		vo.setPaymentStatus(0); // 0 = 未付款
		vo.setPaymentTime(null);
		vo.setOrderCode("ORD" + System.currentTimeMillis() + (new Random().nextInt(9000) + 1000));

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

			ProductVO product = productDAO.findById(pid).orElseThrow(() -> new NoSuchElementException("找不到商品 " + pid));

			// ✅ 檢查是否有足夠庫存可供鎖定
			if (product.getReservedStock() == null)
				product.setReservedStock(0);
			if (product.getStockQuantity() - product.getReservedStock() < qty) {
				throw new IllegalStateException("商品 " + product.getProductName() + " 庫存不足");
			}

			// ✅ 鎖定庫存（reserved_stock 增加）
			product.setReservedStock(product.getReservedStock() + qty);
			productDAO.save(product);

			// 建立訂單明細
			OrderItemVO oit = new OrderItemVO();
			oit.setOrder(vo);
			oit.setProduct(product);
			oit.setOrderItemQuantity(qty);
			oit.setBuyPrice(product.getProductPrice());
			oit.setItemTotalPrice(product.getProductPrice() * qty);
			oit.setOrderItemCode("ITM" + pid + "_" + (new Random().nextInt(900000) + 100000));

			vo.getOrderItems().add(oit);
			total += oit.getItemTotalPrice();
		}

		// 點數使用：不可超過總價
		int safeUsed = Math.max(0, Math.min(usedPts == null ? 0 : usedPts, total));
		vo.setUsedPointsAmount(safeUsed);
		vo.setTotalPrice(total);

		OrdersVO saved = ordersDAO.save(vo);

		// ✅ 清空 Redis 購物車
		cartservice.clear(userId);

		return saved;
	}

	/**
	 * 根據 ID 標示付款成功（單純更新狀態，不含庫存操作）
	 */
	@Transactional
	public OrdersVO markPaid(Integer id) {
		var o = ordersDAO.findById(id).orElseThrow();
		o.setPaymentStatus(1);
		o.setPaymentTime(LocalDateTime.now());
		return o;
	}

	/**
	 * 根據 ID 標示付款失敗（單純更新狀態，不含庫存操作）
	 */
	@Transactional
	public OrdersVO markFailed(Integer id) {
		var o = ordersDAO.findById(id).orElseThrow();
		o.setPaymentStatus(2);
		return o;
	}

	/**
	 * ★★★ 這就是我們要實作的新方法 ★★★ 根據訂單編號 (orderCode) 將訂單標記為已付款
	 *
	 * @param orderCode 來自綠界 Callback 的訂單編號 (MerchantTradeNo)
	 * @return 更新後的訂單物件
	 */
	@Transactional
	public OrdersVO markPaidByOrderCode(String orderCode) {
		// 1. 使用我們剛剛在 DAO 新增的方法，根據 orderCode 找出訂單
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

		// ✅ 扣庫存（把 reserved_stock 一起扣回來）
		for (OrderItemVO item : order.getOrderItems()) {
			ProductVO product = item.getProduct();
			product.setStockQuantity(product.getStockQuantity() - item.getOrderItemQuantity());
			product.setReservedStock(product.getReservedStock() - item.getOrderItemQuantity());
			productDAO.save(product);
		}

		// 4. 將更新後的訂單存回資料庫
		// ★★★ 修正點：將 save() 的回傳值賦回給 order 變數 ★★★
		OrdersVO savedOrder = ordersDAO.save(order);

		return savedOrder;
	}

	/**
	 * 根據 orderCode 標示付款失敗（給綠界/金流回調） ✅ 僅釋放 reserved_stock，不扣庫存
	 */
	@Transactional
	public OrdersVO markFailedByOrderCode(String orderCode) {
		OrdersVO order = ordersDAO.findByOrderCode(orderCode)
				.orElseThrow(() -> new NoSuchElementException("找不到訂單，訂單編號: " + orderCode));

		order.setPaymentStatus(2);

		// ✅ 釋放 reserved_stock
		for (OrderItemVO item : order.getOrderItems()) {
			ProductVO product = item.getProduct();
			product.setReservedStock(product.getReservedStock() - item.getOrderItemQuantity());
			productDAO.save(product);
		}

		return ordersDAO.save(order);
	}
}
