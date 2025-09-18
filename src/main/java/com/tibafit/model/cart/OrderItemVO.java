package com.tibafit.model.cart;

import jakarta.persistence.*;

//	OrderItemVO 是對應 order_item 資料表的 JPA 實體，描述一張訂單中的單筆明細。
//	包含與 OrdersVO（訂單主檔）與 ProductVO（商品）的關聯、數量、成交單價（快照）、小計與明細代碼等欄位。
//	它通常由建立訂單流程寫入：以當下價格/名稱做快照，並記錄 item_total_price（多半為 buyPrice * qty），以確保歷史訂單不受後續商品改價影響。

@Entity // JPA 實體：對應資料表 order_item（訂單「明細」）
@Table(name = "order_item")
public class OrderItemVO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主鍵（MySQL AUTO_INCREMENT）
	@Column(name = "order_item_id")
	private Integer orderItemId;

	@ManyToOne(fetch = FetchType.LAZY) // 多筆明細(N) 對 一張訂單(1)；LAZY 避免不必要抓整張訂單
	@JoinColumn(name = "order_id", nullable = false)
	private OrdersVO order; // N -> 1 orders（父：訂單主檔）

	@ManyToOne(fetch = FetchType.LAZY) // 多筆明細(N) 對 一個商品(1)；LAZY 可減少 JOIN 負擔
	@JoinColumn(name = "product_id", nullable = false)
	private ProductsVO product; // N -> 1 product（關聯商品；建議下單時也把名稱/單價快照存到明細）

	@Column(name = "order_item_quantity", nullable = false) // 數量（>0）
	private Integer orderItemQuantity;

	@Column(name = "buy_price", nullable = false) // 成交單價（快照：下單當下的價格，單位建議統一為「元」整數或「分」整數）
	private Integer buyPrice;

	@Column(name = "item_total_price", nullable = false) // 小計（通常 = buyPrice * orderItemQuantity）
	private Integer itemTotalPrice;

	@Column(name = "order_item_code", nullable = false, unique = true, length = 50) // 明細代碼（若需要對外查詢或對帳）
	private String orderItemCode;

	// ===== Getter / Setter（JavaBean 命名，便於 JPA/Spring/Jackson 使用）=====

	public Integer getOrderItemId() {
		return orderItemId;
	}

	public void setOrderItemId(Integer orderItemId) {
		this.orderItemId = orderItemId;
	}

	public OrdersVO getOrder() {
		return order;
	}

	public void setOrder(OrdersVO order) {
		this.order = order;
	}

	public ProductsVO getProduct() {
		return product;
	}

	public void setProduct(ProductsVO product) {
		this.product = product;
	}

	public Integer getOrderItemQuantity() {
		return orderItemQuantity;
	}

	public void setOrderItemQuantity(Integer orderItemQuantity) {
		this.orderItemQuantity = orderItemQuantity;
	}

	public Integer getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(Integer buyPrice) {
		this.buyPrice = buyPrice;
	}

	public Integer getItemTotalPrice() {
		return itemTotalPrice;
	}

	public void setItemTotalPrice(Integer itemTotalPrice) {
		this.itemTotalPrice = itemTotalPrice;
	}

	public String getOrderItemCode() {
		return orderItemCode;
	}

	public void setOrderItemCode(String orderItemCode) {
		this.orderItemCode = orderItemCode;
	}
}