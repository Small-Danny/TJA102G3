package com.tibafit.model.cart;

import jakarta.persistence.*;

@Entity
@Table(name = "order_item")
public class OrderItemVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_item_id")
	private Integer orderItemId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private OrdersVO order; // N -> 1 orders

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private ProductVO product; // N -> 1 product

	@Column(name = "order_item_quantity", nullable = false)
	private Integer orderItemQuantity;
	
	@Column(name = "buy_price", nullable = false)
	private Integer buyPrice;
	
	@Column(name = "item_total_price", nullable = false)
	private Integer itemTotalPrice;
	
	@Column(name = "order_item_code", nullable = false, unique = true, length = 50)
	private String orderItemCode;

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

	public ProductVO getProduct() {
		return product;
	}

	public void setProduct(ProductVO product) {
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
