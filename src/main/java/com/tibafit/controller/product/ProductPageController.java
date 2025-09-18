package com.tibafit.controller.product;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tibafit.service.product.ProductService;

@Controller
@RequestMapping("/shop")
public class ProductPageController {

	private final ProductService psvc;
	public ProductPageController(ProductService psvc) {
		this.psvc = psvc;
	}
	
	@GetMapping("/products")
    public String list(Model model, @RequestParam(required=false) String q) {
        if (q != null && !q.isBlank())
            model.addAttribute("products", psvc.search(q));
        else
            model.addAttribute("products", psvc.getAll());
        return "frontend/pages/productlist"; //  productlist.html
    }

	@GetMapping("/product/{id}")
	public String detail(@PathVariable Integer id, Model model) {
	    var p = psvc.getOne(id);
	    if (p == null) return "redirect:/shop/products";

	    model.addAttribute("product", p);

	    String code = p.getProductCode();
	    String currentSize = psvc.sizeOf(code); // 可能為 null（均碼或無尺寸）
	    var variants = psvc.findSizeVariantsByCode(code);

	    Map<String, Integer> sizeToId = new LinkedHashMap<>();
	    // 先塞所有變體（同尺寸出現多次只保留第一個）
	    for (var v : variants) {
	        String s = psvc.sizeOf(v.getProductCode());
	        if (s != null) sizeToId.putIfAbsent(s, v.getProductId());
	    }
	    // 自己補上（有尺寸就補；避免只有本體沒顯示）
	    if (currentSize != null) sizeToId.putIfAbsent(currentSize, p.getProductId());

	    // 若完全沒有尺寸資訊 => 視為均碼
	    if (sizeToId.isEmpty()) {
	        currentSize = null;
	    }

	    var sizes = new java.util.ArrayList<>(sizeToId.keySet());

	    model.addAttribute("currentSize", currentSize);
	    model.addAttribute("sizeToId", sizeToId);
	    model.addAttribute("sizes", sizes);

	    return "frontend/pages/productdetail";
	}
	
	@GetMapping("/api/stock")
	@ResponseBody
	public Map<String, Object> stock(@RequestParam("productId") Integer productId) {
	    var p = psvc.getOne(productId);
	    if (p == null) {
	        return Map.of("ok", false, "msg", "not found");
	    }
	    return Map.of("ok", true, "stock", p.getStockQuantity());
	}
}
