package com.tibafit.service.cart;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tibafit.model.cart.OrderItemVO;
import com.tibafit.model.cart.OrdersVO;
import com.tibafit.model.cart.ProductsVO;
import com.tibafit.repository.cart.OrderItemDAO;
import com.tibafit.repository.cart.OrdersDAO;
import com.tibafit.repository.cart.ProductDAO;

//	新增明細（可指定單價，否則抓上架價）、修改數量、刪除明細。
//	每次操作都會同步更新訂單總價。

@Service // 訂單明細維護服務（後台增/修/刪明細 + 調整訂單總價）
public class OrderItemService {

	private final OrdersDAO ordersDAO; // 操作訂單主檔
	private final ProductDAO productDAO; // 查商品（價格/上架狀態等）
	private final OrderItemDAO orderItemDAO; // 操作訂單明細

	@Autowired
	public OrderItemService(OrdersDAO ordersDAO, ProductDAO productDAO, OrderItemDAO orderItemDAO) {
		this.ordersDAO = ordersDAO;
		this.productDAO = productDAO;
		this.orderItemDAO = orderItemDAO;
	}

	/** 依明細 id 取單筆，找不到就丟 400-ish（上層會轉 ProblemDetail） */
	public OrderItemVO get(Integer id) {
		return orderItemDAO.findById(id).orElseThrow(() -> new IllegalStateException("找不到訂單明細 ID " + id));
	}

	/** 列出某張訂單的所有明細 */
	public List<OrderItemVO> listByOrder(Integer orderId) {
		return orderItemDAO.findByOrder_OrderId(orderId);
	}

	/**
	 * 在既有訂單中新增一筆明細 - 若 buyPrice 為 null：用「上架時的售價」做快照 - 會同步更新訂單總價（totalPrice）
	 */
	@Transactional
	public OrdersVO addItem(Integer orderId, Integer productId, Integer quantity, Integer buyPrice) {
		OrdersVO order = ordersDAO.findById(orderId)
				.orElseThrow(() -> new IllegalStateException("找不到訂單 ID " + orderId));

		// 取單價（允許後台指定 buyPrice；否則以上架價為準）
		Integer price = (buyPrice != null) ? buyPrice : productDAO.findOnShelfPrice(productId);
		if (price == null)
			throw new IllegalStateException("商品 " + productId + " 不存在或未上架");
		if (quantity == null || quantity <= 0)
			throw new IllegalStateException("數量須大於 0");

		OrderItemVO it = new OrderItemVO();
		it.setOrder(order); // 維護雙向關聯（子指向父）

		// ⚠ 這裡以「new ProductVO + 設 id」掛外鍵，Hibernate 有機會視為 transient 物件而報錯
		// 建議改用：productDAO.getReferenceById(productId)（不命中 DB，直接給代理，安全掛 FK）
		ProductsVO ref = new ProductsVO();
		ref.setProductId(productId);
		it.setProduct(ref);

		it.setOrderItemQuantity(quantity);
		it.setBuyPrice(price);
		it.setItemTotalPrice(price * quantity);
		it.setOrderItemCode("ITM" + productId + "_" + (new Random().nextInt(900000) + 100000)); // 簡易碼（若需保證唯一可先查重）

		// 把明細掛到訂單底下（因 orders@OneToMany(cascade=ALL)，flush 時會一併存）
		order.getOrderItems().add(it);

		// 同步更新訂單總價（避免 null）
		order.setTotalPrice((order.getTotalPrice() == null ? 0 : order.getTotalPrice()) + it.getItemTotalPrice());

		return order; // 交易結束時 JPA flush
	}

	/**
	 * 修改明細數量（會重算小計，並調整訂單總價）
	 */
	@Transactional
	public OrdersVO updateQuantity(Integer itemId, Integer newQty) {
		if (newQty == null || newQty <= 0)
			throw new IllegalStateException("數量須大於 0");

		OrderItemVO it = get(itemId);
		OrdersVO order = it.getOrder();

		int old = it.getItemTotalPrice(); // 舊小計
		it.setOrderItemQuantity(newQty);
		it.setItemTotalPrice(it.getBuyPrice() * newQty); // 新小計
		order.setTotalPrice(order.getTotalPrice() - old + it.getItemTotalPrice()); // 調整總價

		return order;
	}

	/**
	 * 移除一筆明細（會調整訂單總價） orphanRemoval=true：從集合中移除後，flush 時會自動刪除該明細
	 */
	@Transactional
	public OrdersVO removeItem(Integer itemId) {
		OrderItemVO it = get(itemId);
		OrdersVO order = it.getOrder();

		order.setTotalPrice(order.getTotalPrice() - it.getItemTotalPrice()); // 調整總價
		order.getOrderItems().remove(it); // 觸發 orphanRemoval

		return order;
	}
}
