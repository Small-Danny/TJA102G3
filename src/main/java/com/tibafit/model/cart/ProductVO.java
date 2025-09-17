package com.tibafit.model.cart;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "product")
public class ProductVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private Integer productId;

	@Column(name = "product_type", nullable = false)
	private Integer productType;
	@Column(name = "product_name", nullable = false, length = 255)
	private String productName;
	@Column(name = "product_description", nullable = false, length = 255)
	private String productDescription;
	@Column(name = "product_price", nullable = false)
	private Integer productPrice;
	@Column(name = "stock_quantity", nullable = false)
	private Integer stockQuantity;
	@Column(name = "product_picture", nullable = false, length = 255)
	private String productPicture;
	@Column(name = "product_status", nullable = false)
	private Integer productStatus; // 0下架 1上架
	@Column(name = "product_code", nullable = false, unique = true, length = 50)
	private String productCode;

	@OneToMany(mappedBy = "product") // product (1) -> order_item (N)
	private List<OrderItemVO> orderItems;

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getProductType() {
		return productType;
	}

	public void setProductType(Integer productType) {
		this.productType = productType;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public Integer getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(Integer productPrice) {
		this.productPrice = productPrice;
	}

	public Integer getStockQuantity() {
		return stockQuantity;
	}

	public void setStockQuantity(Integer stockQuantity) {
		this.stockQuantity = stockQuantity;
	}

	public String getProductPicture() {
		return productPicture;
	}

	public void setProductPicture(String productPicture) {
		this.productPicture = productPicture;
	}

	public Integer getProductStatus() {
		return productStatus;
	}

	public void setProductStatus(Integer productStatus) {
		this.productStatus = productStatus;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public List<OrderItemVO> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItemVO> orderItems) {
		this.orderItems = orderItems;
	}
	
	
}
