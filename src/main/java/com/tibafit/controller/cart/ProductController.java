package com.tibafit.controller.cart;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tibafit.model.cart.ProductVO;
import com.tibafit.service.cart.ProductService;

//	ProductController 提供前台/共用的商品查詢 API，用逗號分隔的 ids 參數批次查詢商品並回傳清單

@RestController // REST 控制器：方法回傳值會被序列化成 JSON
@RequestMapping("/api/products") // 本控制器的共同前綴
public class ProductController {
	private final ProductService productService; // 商品服務（查詢商品資料）

	@Autowired // 建構子注入：由 Spring 負責注入 ProductService Bean
	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	/**
	 * 依多個 id 查商品清單（逗號分隔） 範例：GET /api/products?ids=1,2,3 備註： - 目前直接回傳
	 * Entity（ProductVO）；若要避免曝露內部欄位，建議改回傳 ProductDTO。 - 若只想回「上架商品」，可在 Service/DAO
	 * 層過濾 productStatus=1。
	 */
	@GetMapping
	public List<ProductVO> listProducts(@RequestParam String ids) {
		// 將 query string 的 "1,2,3" 切成 ["1","2","3"] → 轉成 [1,2,3]
		// 注意：若遇到空字串或非數字會丟 NumberFormatException；必要時可在這裡做防呆。
		List<Integer> idList = Arrays.stream(ids.split(",")).map(Integer::valueOf).toList();

		// 交給服務層查詢（常見作法：JPA 的 findAllById 或自訂 findOnShelfByIds）
		return productService.findAllByIds(idList);
	}
}
