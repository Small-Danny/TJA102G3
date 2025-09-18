package com.tibafit.dto.product;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

//	ProductCreateDTO 是後台新增商品時用的請求資料模型。包含 SKU、名稱、價格（BigDecimal）、庫存、狀態（上/下架）、類別、描述與圖片路徑。
//	配合 Controller 的 @Valid，可先擋下必填/長度/非負等不合法輸入；服務層再將其轉換為 ProductVO 實際入庫。
//	小提醒：你在 ProductAdminController 將 productPrice 以 intValue() 存到 VO，請統一金額的小數處理規則（例如一律以「元」整數或「分」整數儲存），避免精度/四捨五入爭議。

@NoArgsConstructor // Lombok：產生無參數建構子
@AllArgsConstructor // Lombok：產生全參數建構子
@Builder // Lombok：產生 builder()（若使用，欄位名需與本類一致）
public class ProductCreateDTO {

	@NotBlank(message = "商品代碼必填")
	@Size(max = 50)
	private String productCode; // 產品代碼（SKU）；可視需要加 @Pattern 避免特殊字元

	@NotBlank(message = "商品名稱必填")
	@Size(max = 255)
	private String productName; // 產品名稱

	@NotNull(message = "價格必填")
	@PositiveOrZero(message = "價格不可為負")
	private BigDecimal productPrice; // 售價（以 BigDecimal 表示金額較安全）
										// ⚠ 你在 Controller 會轉成 int 存到 VO：請注意四捨五入/小數位（建議統一規則）

	@NotNull(message = "庫存必填")
	@PositiveOrZero(message = "庫存不可為負")
	private Integer stockQuantity; // 庫存數量（整數，允許 0）

	// 預設 1=上架；若前端不送可在 Service 給預設值
	private Integer productStatus; // 1=上架, 0=下架（可考慮使用 Enum/常數集中管理）

	@NotNull
	private Integer productType; // 類別（建議配合前端選單或後端 enum 驗證）

	@NotBlank
	@Size(max = 255)
	private String productDescription; // 簡述（前端詳情頁可放更長內容到別欄）

	@NotBlank
	@Size(max = 255)
	private String productPicture; // 圖片 URL/路徑；若使用外部連結可考慮 @URL（Hibernate Validator）

	// ===== Getter / Setter（JavaBean 命名，便於 Spring/Jackson 綁定）=====

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
