package com.tibafit.model.cart;

import jakarta.persistence.*;
import java.util.List;

//	ProductVO 是商品主檔的 JPA 實體，對應 product 表，包含類別、名稱、描述、價格、庫存、圖片、上下架
//	狀態與 SKU 等欄位，並透過 orderItems 反向關聯到訂單明細。後台 CRUD 與前台查詢都會以此為資料來
//	源；建議將 productStatus/productType 以常數或 Enum 管理，價格單位統一（整數元或分）。

@Entity // JPA 實體：對應資料表 product（商品主檔）
@Table(name = "product")
public class ProductsVO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主鍵（MySQL AUTO_INCREMENT）
	@Column(name = "product_id")
	private Integer productId;

	@Column(name = "product_type", nullable = false) // 商品類別（建議以常數/Enum 管理）
	private Integer productType;

	@Column(name = "product_name", nullable = false, length = 255) // 商品名稱
	private String productName;

	@Column(name = "product_description", nullable = false, length = 255) // 商品描述（摘要）
	private String productDescription;

	@Column(name = "product_price", nullable = false) // 售價（單位建議統一：整數元或分）
	private Integer productPrice;

	@Column(name = "stock_quantity", nullable = false) // 庫存數量（>=0）
	private Integer stockQuantity;

	@Column(name = "product_picture", nullable = false, length = 255) // 圖片 URL/路徑
	private String productPicture;

	@Column(name = "product_status", nullable = false) // 商品狀態：0=下架, 1=上架
	private Integer productStatus; // 建議用常數/Enum 集中管理以避免魔術數字

	@Column(name = "product_code", nullable = false, unique = true, length = 50) // SKU/商品代碼（唯一）
	private String productCode;

	@OneToMany(mappedBy = "product") // 一對多：product (1) -> order_item (N)（預設 LAZY）
	private List<OrderItemVO> orderItems; // 被哪些訂單明細參考（通常不在前台直接序列化出去）

	// ===== Getter / Setter（JavaBean 命名，便於 JPA/Spring/Jackson 使用）=====

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
