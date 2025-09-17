package com.tibafit.dto.product;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreateDTO {
	
	@NotBlank(message = "商品代碼必填")
    @Size(max = 50)
    private String productCode;

    @NotBlank(message = "商品名稱必填")
    @Size(max = 255)
    private String productName;

    @NotNull(message = "價格必填")
    @PositiveOrZero(message = "價格不可為負")
    private BigDecimal productPrice;

    @NotNull(message = "庫存必填")
    @PositiveOrZero(message = "庫存不可為負")
    private Integer stockQuantity;

    // 預設 1=上架；若前端不送可在 Service 給預設
    private Integer productStatus;
    
    @NotNull
    private Integer productType;
    
    @NotBlank @Size(max = 255)
    private String productDescription;
    
    @NotBlank
    @Size(max = 255)
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

	public BigDecimal getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(BigDecimal productPrice) {
		this.productPrice = productPrice;
	}

	public Integer getStockQuantity() {
		return stockQuantity;
	}

	public void setStockQuantity(Integer stockQuantity) {
		this.stockQuantity = stockQuantity;
	}

	public Integer getProductStatus() {
		return productStatus;
	}

	public void setProductStatus(Integer productStatus) {
		this.productStatus = productStatus;
	}
    
    
}
