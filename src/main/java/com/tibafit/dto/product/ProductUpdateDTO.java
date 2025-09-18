package com.tibafit.dto.product;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

//	ProductUpdateDTO 是後台更新商品時用的請求模型。必填 productId（指定要改哪一筆），其餘欄位帶入後
//	由 Service 實作更新。你目前的設計將 productType / productDescription / productPicture 設為必填，其餘
//	欄位可選；如果想支援「部分更新」（例如 PATCH 風格），可以把這些欄位也改成可為 null，在 Service
//	只更新非 null 欄位，或改用 @PatchMapping 的端點以符合語意。
//	另外，productPrice 為 BigDecimal，但存入 VO 時轉為 int，建議統一金額單位（全程整數元或分），避免精度/四捨五入爭議。

@NoArgsConstructor // Lombok：無參數建構子
@AllArgsConstructor // Lombok：全參數建構子
@Builder // Lombok：builder()（若使用，欄位名需與本類一致）
public class ProductUpdateDTO {

	@NotNull(message = "商品ID必填")
	private Integer productId; // 要更新的商品主鍵

	@Size(max = 50)
	private String productCode; // SKU/代碼（可選；不填代表不更新或由 Service 決策）

	@Size(max = 255)
	private String productName; // 名稱（可選）

	@PositiveOrZero
	private BigDecimal productPrice; // 售價（可選；建議後端統一小數處理規則）
										// 你在 Controller 會轉 int 存 VO：注意四捨五入/小數位一致性

	@PositiveOrZero
	private Integer stockQuantity; // 庫存（可選）

	/** 1=上架,0=下架 */
	private Integer productStatus; // 狀態（可選；建議用常數/Enum 集中管理）

	@NotNull
	private Integer productType; // 類別（此 DTO 要求必填；若想支援部分更新，可改成可為 null）

	@NotBlank
	@Size(max = 255)
	private String productDescription; // 描述（此 DTO 要求必填；與上方同理，視需求調整是否必填）

	@NotBlank
	@Size(max = 255)
	private String productPicture; // 圖片 URL/路徑（此 DTO 要求必填）

	// ===== Getter / Setter（JavaBean 命名，便於 Spring/Jackson 綁定）=====

	public Integer getProductType() {
		return productType;
	}

	public void setProductType(Integer productType) {
		this.productType = productType;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public String getProductPicture() {
		return productPicture;
	}

	public void setProductPicture(String productPicture) {
		this.productPicture = productPicture;
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
