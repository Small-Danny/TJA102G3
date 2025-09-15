package com.tibafit.dto.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.tibafit.model.cart.ProductVO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
	private String productDescription;
	private Integer productType;
	private Integer productId;
	private String productCode;
	private String productName;
	private Integer productStatus; // 1=上架, 0=下架
	private Integer productPrice;
	private Integer stockQuantity;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String productPicture;

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

	public static ProductDTO from(ProductVO vo) {
		ProductDTO dto = new ProductDTO();
		dto.setProductId(vo.getProductId());
		dto.setProductType(vo.getProductType());
		dto.setProductName(vo.getProductName());
		dto.setProductDescription(vo.getProductDescription());
		dto.setProductPrice(vo.getProductPrice());
		dto.setStockQuantity(vo.getStockQuantity());
		dto.setProductPicture(vo.getProductPicture());
		dto.setProductStatus(vo.getProductStatus());
		dto.setProductCode(vo.getProductCode());
		return dto;
	}

}
