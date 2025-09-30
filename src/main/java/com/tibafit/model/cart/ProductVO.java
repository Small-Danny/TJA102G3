package com.tibafit.model.cart; // 建議統一放在這個 package

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

//  ProductVO 是商品主檔的 JPA 實體，對應 product 表，包含類別、名稱、描述、價格、庫存、圖片、上下架
//  狀態與 SKU 等欄位，並透過 orderItems 反向關聯到訂單明細。後台 CRUD 與前台查詢都會以此為資料來
//  源；建議將 productStatus/productType 以常數或 Enum 管理，價格單位統一（整數元或分）。

@Entity
@Table(name = "product")
// ★★★ 修改點 1 & 2：重新命名為 ProductVO 並加上 implements Serializable ★★★
public class ProductVO implements Serializable {

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
	private Integer productStatus;

	// 我們之前比較時發現長度不一致，這裡保留註解完整的版本 (50)，若資料庫是 64，也可改為 64
	@Column(name = "product_code", nullable = false, unique = true, length = 50)
	private String productCode;

	@OneToMany(mappedBy = "product")
	private List<OrderItemVO> orderItems;

	@Column(name = "reserved_stock", nullable = false)
	private Integer reservedStock;

	// ===== Getter / Setter (以下完全不用動) =====

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

	public Integer getReservedStock() {
		return reservedStock;
	}

	public void setReservedStock(Integer reservedStock) {
		this.reservedStock = reservedStock;
	}

}