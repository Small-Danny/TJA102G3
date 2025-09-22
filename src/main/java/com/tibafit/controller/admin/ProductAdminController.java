package com.tibafit.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tibafit.dto.product.ProductCreateDTO;
import com.tibafit.dto.product.ProductDTO;
import com.tibafit.dto.product.ProductUpdateDTO;
import com.tibafit.model.cart.ProductVO;
import com.tibafit.service.cart.ProductServices;

import jakarta.validation.Valid;

//	新增商品、查單筆、條件＋分頁查詢。
//	全量更新商品資訊（PUT）。
//	刪除商品（或可替換成下架）。
//	調整庫存（PATCH，支援正負增減）。
//	整體給管理者後台（如 AdminLTE）使用

@RestController // 宣告為 REST 控制器，方法回傳值會序列化成 JSON
@RequestMapping("/api/admin/products") // 本控制器的共同路徑前綴
public class ProductAdminController {
	private final ProductServices productServices; // 商品領域服務：封裝新增/查/改/刪與進階邏輯

	@Autowired // 建構子注入：Spring 會自動注入對應的 Service Bean
	public ProductAdminController(ProductServices productServices) {
		this.productServices = productServices;
	}

	/** 新增商品（後台建立） */
	@PostMapping
	public ProductDTO create(@RequestBody @Valid ProductCreateDTO req) {
		// 將前端傳入的建立用 DTO → 組成 Entity（VO）
		ProductVO vo = new ProductVO();
		vo.setProductType(req.getProductType()); // 類別
		vo.setProductName(req.getProductName()); // 名稱
		vo.setProductDescription(req.getProductDescription()); // 描述
		vo.setProductPrice(req.getProductPrice().intValue()); // 價格（若 DTO 用 BigDecimal，這裡轉 int）
		vo.setStockQuantity(req.getStockQuantity()); // 庫存
		vo.setProductPicture(req.getProductPicture()); // 圖片 URL/路徑
		vo.setProductStatus(req.getProductStatus()); // 狀態：1上架/0下架
		vo.setProductCode(req.getProductCode()); // 自訂商品代碼（SKU）
		// 交給服務層處理（含驗證/商業規則/儲存），回傳結果轉成乾淨的 DTO
		return ProductDTO.from(productServices.create(vo));
	}

	/** 查單筆商品 */
	@GetMapping("/{id}")
	public ProductDTO get(@PathVariable Integer id) {
		// 服務層取得 Entity → 轉 DTO 回前端
		return ProductDTO.from(productServices.get(id));
	}

	/**
	 * 分頁查商品（可選條件）
	 * 
	 * @param status  過濾商品狀態（如 1=上架, 0=下架；可為 null 表示不過濾）
	 * @param keyword 關鍵字（通常用於名稱/代碼查詢；可為 null）
	 * @param page    第幾頁（0-based）
	 * @param size    每頁大小
	 */
	@GetMapping
	public Page<ProductDTO> list(@RequestParam(required = false) Integer status,
			@RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		// 由服務層回傳 Page<ProductVO>，在此 map 成 Page<ProductDTO>
		return productServices.list(status, keyword, page, size).map(ProductDTO::from);
	}

	/** 全量更新（PUT）：用 DTO 覆寫指定商品的主要欄位 */
	@PutMapping("/{id}")
	public ProductDTO update(@PathVariable Integer id, @RequestBody @Valid ProductUpdateDTO req) {
		// 用一個新的 VO 承接更新資料（服務層會把它 merge 到既有資料）
		ProductVO data = new ProductVO();
		data.setProductType(req.getProductType());
		data.setProductName(req.getProductName());
		data.setProductDescription(req.getProductDescription());
		data.setProductPrice(req.getProductPrice().intValue());
		data.setStockQuantity(req.getStockQuantity());
		data.setProductPicture(req.getProductPicture());
		data.setProductStatus(req.getProductStatus());
		data.setProductCode(req.getProductCode());
		return ProductDTO.from(productServices.update(id, data));
	}

	/** 刪除商品（注意：實務上常做軟刪/下架，而非物理刪除） */
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Integer id) {
		productServices.delete(id);
	}

	/**
	 * 調整庫存（PATCH）：delta 可正可負 例：PATCH /api/admin/products/10/stock?delta=5 → 庫存 +5
	 * PATCH /api/admin/products/10/stock?delta=-3 → 庫存 -3
	 */
	@PatchMapping("/{id}/stock")
	public ProductDTO adjustStock(@PathVariable Integer id, @RequestParam int delta) {
		return ProductDTO.from(productServices.adjustStock(id, delta));
	}
}