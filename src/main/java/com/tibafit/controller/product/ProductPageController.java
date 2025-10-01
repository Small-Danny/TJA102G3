package com.tibafit.controller.product;

import java.util.*;
import com.tibafit.model.cart.ProductVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.tibafit.service.product.ProductService;

@Controller
@RequestMapping("/shop")
public class ProductPageController {

	private final ProductService psvc;

	public ProductPageController(ProductService psvc) {
		this.psvc = psvc;
	}

	@GetMapping("/products")
	public String list(Model model, @RequestParam(required = false) String q) {
		if (q != null && !q.isBlank()) {
			model.addAttribute("products", psvc.searchCollapsed(q));
		} else {
			model.addAttribute("products", psvc.getAllCollapsed());
		}
		return "frontend/pages/productlist";
	}

	/** 商品詳情（支援 ?size=） */
	@GetMapping("/product/{id}")
	public String detail(@PathVariable Integer id, @RequestParam(name = "size", required = false) String sizeQry,
			Model model) {

		ProductVO p = psvc.getOne(id);
		if (p == null)
			return "redirect:/shop/products";

		model.addAttribute("product", p);

		// 依商品型別決定沒單位的 fallback 單位
		String fallbackUnit = psvc.defaultUnitFor(p);

		// 蒐集同款所有變體
		List<ProductVO> variants = psvc.findSizeVariantsByCode(p.getProductCode());

		// 建立 map
		Map<String, Integer> sizeMap = new LinkedHashMap<>();
		Map<String, String> sizeSkuMap = new LinkedHashMap<>();
		Map<String, Integer> sizePriceMap = new LinkedHashMap<>();

		for (ProductVO v : variants) {
			String sz = psvc.sizeOf(v.getProductCode(), psvc.defaultUnitFor(v));
			if (sz == null)
				continue;
			String key = sz.trim().toUpperCase();

			sizeSkuMap.putIfAbsent(key, v.getProductCode());
			sizePriceMap.putIfAbsent(key, v.getProductPrice());

			// productStatus == 1（上架）才給 pid，否則為 null（前端自動灰掉）
			if (v.getProductStatus() != null && v.getProductStatus() == 1) {
				sizeMap.putIfAbsent(key, Objects.equals(v.getProductStatus(), 1) ? v.getProductId() : null);
			} else {
				sizeMap.putIfAbsent(key, null);
			}
		}

		// 確保自己也在 map 中（防止漏掉）
		String selfSize = psvc.sizeOf(p.getProductCode(), fallbackUnit);
		if (selfSize != null) {
			String key = selfSize.trim().toUpperCase();
			sizeSkuMap.putIfAbsent(key, p.getProductCode());
			sizePriceMap.putIfAbsent(key, p.getProductPrice());
			if (!sizeMap.containsKey(key)) {
				sizeMap.put(key, (p.getProductStatus() != null && p.getProductStatus() == 1) ? p.getProductId() : null);
			}
		}

		// 若完全沒有尺寸 → 均碼
		if (sizeMap.isEmpty()) {
			sizeMap.put("均碼", (p.getProductStatus() != null && p.getProductStatus() == 1) ? p.getProductId() : null);
			sizeSkuMap.put("均碼", p.getProductCode());
			sizePriceMap.put("均碼", p.getProductPrice());
		}

		// 排序尺寸
		List<String> sizesSorted = psvc.sortSizes(sizeMap.keySet());

		// 決定目前尺寸（優先順序：URL ?size > 自身尺寸 > 第一個）
		String currentSize = resolveCurrentSizeWithFallback(p, sizeQry, sizeMap, fallbackUnit);
		String currentSku = sizeSkuMap.getOrDefault(currentSize, p.getProductCode());
		Integer currentPrice = sizePriceMap.getOrDefault(currentSize, p.getProductPrice());

		// 傳給前端
		model.addAttribute("sizes", sizesSorted);
		model.addAttribute("sizeMap", sizeMap);
		model.addAttribute("sizeSkuMap", sizeSkuMap);
		model.addAttribute("currentSize", currentSize);
		model.addAttribute("currentSku", currentSku);
		model.addAttribute("sizePriceMap", sizePriceMap);
		model.addAttribute("currentPrice", currentPrice);

		return "frontend/pages/productdetail";
	}

	/** 供庫存查詢 API */
	@GetMapping("/api/stock")
	@ResponseBody
	public Map<String, Object> stock(@RequestParam("productId") Integer productId) {
		ProductVO p = psvc.getOne(productId);
		if (p == null) {
			return Map.of("ok", false, "msg", "not found");
		}

		// ★ 若商品已下架（productStatus != 1），一律回 0
		int stock = (p.getProductStatus() != null && p.getProductStatus() == 1)
				? Optional.ofNullable(p.getStockQuantity()).orElse(0)
				: 0;

		return Map.of("ok", true, "stock", stock);
	}

	/** 判斷此變體是否可售（上架+庫存>0） */
	private boolean isSellable(Integer pid) {
		if (pid == null)
			return false;
		ProductVO v = psvc.getOne(pid);
		if (v == null)
			return false;
		boolean up = Objects.equals(v.getProductStatus(), 1);
		int stock = Optional.ofNullable(v.getStockQuantity()).orElse(0);
		return up && stock > 0;
	}

	/** 僅判斷是否上架 */
	private boolean isUp(Integer pid) {
		if (pid == null)
			return false;
		ProductVO v = psvc.getOne(pid);
		return v != null && Objects.equals(v.getProductStatus(), 1);
	}

	/** 以 fallback 單位輔助決定目前尺寸（優先可售，其次上架，其次第一個） */
	private String resolveCurrentSizeWithFallback(ProductVO product, String sizeQry, Map<String, Integer> sizeToId,
			String fallbackUnit) {
		if (sizeToId == null || sizeToId.isEmpty())
			return "均碼";

		// 1) URL ?size=：若可售 -> 用它；若上架 -> 用它
		if (sizeQry != null) {
			String key = sizeQry.trim().toUpperCase();
			Integer pid = sizeToId.get(key);
			if (pid != null) {
				if (isSellable(pid))
					return key;
				if (isUp(pid))
					return key;
			}
		}

		// 2) 自身尺寸（帶 fallback）：若可售 -> 用它；若上架 -> 用它
		String self = psvc.sizeOf(product.getProductCode(), fallbackUnit);
		if (self != null) {
			String key = self.trim().toUpperCase();
			Integer pid = sizeToId.get(key);
			if (pid != null) {
				if (isSellable(pid))
					return key;
				if (isUp(pid))
					return key;
			}
		}

		// 3) 在所有尺寸裡找第一個可售
		for (Map.Entry<String, Integer> e : sizeToId.entrySet()) {
			if (isSellable(e.getValue()))
				return e.getKey();
		}

		// 4) 找第一個上架
		for (Map.Entry<String, Integer> e : sizeToId.entrySet()) {
			if (isUp(e.getValue()))
				return e.getKey();
		}

		// 5) 都不行就回第一個（可能是下架，前端會呈現禁用）
		return sizeToId.keySet().iterator().next();
	}

}
