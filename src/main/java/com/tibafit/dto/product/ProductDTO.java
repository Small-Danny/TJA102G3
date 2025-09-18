package com.tibafit.dto.product;

import java.time.LocalDateTime;

import com.tibafit.model.cart.ProductsVO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

//	ProductDTO 是前端展示與後台列表用的「乾淨商品資料模型」。它把資料庫 Entity（ProductVO）轉成前端
//	需要的欄位，避免曝露內部不該給前端看的資訊；透過 from(ProductVO) 進行集中映射，讓回傳格式穩定一
//	致。建議金額欄位統一規則（整數元或以分為單位的整數／或改用 BigDecimal），並視需要補上
//	createdAt/updatedAt 的映射。

@Builder // Lombok：支援 ProductDTO.builder() 建構
@NoArgsConstructor // Lombok：無參數建構子
@AllArgsConstructor // Lombok：全參數建構子
public class ProductDTO {
	private String productDescription; // 商品描述（摘要）
	private Integer productType; // 商品類別（可對應後端 enum/code）
	private Integer productId; // 商品主鍵
	private String productCode; // SKU / 內部代碼
	private String productName; // 商品名稱
	private Integer productStatus; // 狀態：1=上架, 0=下架
	private Integer productPrice; // 售價（以「元」整數儲存；若要小數請改 BigDecimal）
	private Integer stockQuantity; // 庫存數量
	private LocalDateTime createdAt; // 建立時間（若 VO 有再映射）
	private LocalDateTime updatedAt; // 更新時間（若 VO 有再映射）
	private String productPicture; // 圖片 URL/路徑

	public String getProductPicture() {
		return productPicture;
	}

	public void setProductPicture(String productPicture) {
		this.productPicture = productPicture;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public Integer getProductType() {
		return productType;
	}

	public void setProductType(Integer productType) {
		this.productType = productType;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getProductStatus() {
		return productStatus;
	}

	public void setProductStatus(Integer productStatus) {
		this.productStatus = productStatus;
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	/**
	 * 將 JPA Entity（ProductVO）轉為前端友善的 DTO。 - 僅映射你目前 VO 中常用欄位；若 VO 有
	 * createdAt/updatedAt 也可在此處補上。 - 如需遮蔽內部欄位（成本價等），請勿加入 DTO。
	 */
	public static ProductDTO from(ProductsVO vo) {
		ProductDTO dto = new ProductDTO();
		dto.setProductId(vo.getProductId());
		dto.setProductType(vo.getProductType());
		dto.setProductName(vo.getProductName());
		dto.setProductDescription(vo.getProductDescription());
		dto.setProductPrice(vo.getProductPrice()); // 以整數元回傳
		dto.setStockQuantity(vo.getStockQuantity());
		dto.setProductPicture(vo.getProductPicture());
		dto.setProductStatus(vo.getProductStatus());
		dto.setProductCode(vo.getProductCode());

		// 若 ProductVO 有建立/更新時間欄位，可在這裡一併映射：
		// dto.setCreatedAt(vo.getCreatedAt());
		// dto.setUpdatedAt(vo.getUpdatedAt());

		return dto;
	}
}
