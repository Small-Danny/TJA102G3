package com.tibafit.controller.cart;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tibafit.dto.product.ProductDTO;
import com.tibafit.service.cart.ProductServices;

//	ProductController 提供前台/共用的商品查詢 API，用逗號分隔的 ids 參數批次查詢商品並回傳清單

@RestController // REST 控制器：方法回傳值會被序列化成 JSON
@RequestMapping("/api/products") // 本控制器的共同前綴
public class ProductsController {
	private final ProductServices productServices; // 商品服務（查詢商品資料）

	@Autowired // 建構子注入：由 Spring 負責注入 ProductService Bean
	public ProductsController(ProductServices productServices) {
		this.productServices = productServices;
	}

	/**
	 * 依多個 id 查商品清單（逗號分隔） 範例：GET /api/products?ids=1,2,3 備註： - 目前直接回傳
	 * Entity（ProductVO）；若要避免曝露內部欄位，建議改回傳 ProductDTO。 - 若只想回「上架商品」，可在 Service/DAO
	 * 層過濾 productStatus=1。
	 */
	@GetMapping
	public List<ProductDTO> listProducts(@RequestParam String ids) {
		List<Integer> idList = Arrays.stream(ids.split(",")).map(Integer::valueOf).toList();

		return productServices.findAllByIds(idList).stream().map(ProductDTO::from).toList();
	}
}
