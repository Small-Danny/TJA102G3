package com.tibafit.controller.cart;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tibafit.dto.cart.CartAddItemDTO;
import com.tibafit.dto.cart.CartDTO;
import com.tibafit.dto.cart.CartSetQuantityDTO;
import com.tibafit.model.cart.ProductVO;
import com.tibafit.service.cart.CartService;
import com.tibafit.service.cart.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	private final CartService cartService;
	private final ProductService productService;

	@Autowired
	public CartController(CartService cartService, ProductService productService) {
		this.cartService = cartService;
		this.productService = productService;
	}

	// ✅ 新增這支：訂單頁要用的「購物車摘要」
	@GetMapping("/{userId}/summary")
	public Map<String, Object> getSummary(@PathVariable Integer userId) {
		// 1) 從 Redis 取購物車：形如 { productId -> quantity }
		// ⛳️ 方法名請改成你 CartService 目前的名稱，例如 getCartFromRedis / getCartMap 等
		Map<Object, Object> cart = cartService.getCart(userId);

		// 沒資料就回空
		if (cart == null || cart.isEmpty()) {
			Map<String, Object> resp = new LinkedHashMap<>();
			resp.put("items", List.of());
			resp.put("totalQuantity", 0);
			resp.put("totalAmount", 0);
			return resp;
		}

		// 2) 撈商品資訊
		List<Integer> ids = cart.keySet().stream().map(k -> Integer.valueOf(k.toString())).toList();

		// ⛳️ 方法名請對齊你的 ProductService，常見是 findAllById(List<Integer>)
		List<ProductVO> products = productService.findAllByIds(ids);

		Map<Integer, ProductVO> pmap = products.stream()
				.collect(Collectors.toMap(ProductVO::getProductId, Function.identity()));

		// 3) 組 items + 計總金額/數量
		List<Map<String, Object>> items = new ArrayList<>();
		int totalQty = 0;
		int totalAmt = 0;

		for (var e : cart.entrySet()) {
			Integer pid = Integer.valueOf(e.getKey().toString());
			Integer qty = (Integer) e.getValue();
			if (qty == null || qty <= 0)
				continue;

			ProductVO p = pmap.get(pid);
			if (p == null)
				continue;

			int price = p.getProductPrice(); // ⛳️ 對齊你的欄位
			int sub = price * qty;

			Map<String, Object> line = new LinkedHashMap<>();
			line.put("productId", pid);
			line.put("productName", p.getProductName()); // ⛳️ 對齊欄位
			line.put("unitPrice", price);
			line.put("quantity", qty);
			line.put("subtotal", sub);

			items.add(line);
			totalQty += qty;
			totalAmt += sub;
		}

		Map<String, Object> resp = new LinkedHashMap<>();
		resp.put("items", items);
		resp.put("totalQuantity", totalQty);
		resp.put("totalAmount", totalAmt);
		return resp;
	}

	// GET /api/cart/cart-items?userId=1
	@GetMapping("/cart-items")
	public CartDTO get(@RequestParam Integer userId) {
		return CartDTO.fromCartMap(cartService.getCart(userId));
	}

	// POST /api/cart/items
	@PostMapping("/items")
	public CartDTO add(@RequestBody @Valid CartAddItemDTO req) {
		cartService.addItem(req.getUserId(), req.getProductId(), req.getQty());
		return CartDTO.fromCartMap(cartService.getCart(req.getUserId()));
	}

	// PUT /api/cart/items
	@PutMapping("/items")
	public CartDTO set(@RequestBody @Valid CartSetQuantityDTO req) {
		cartService.setQuantity(req.getUsertId(), req.getProductId(), req.getQty());
		return CartDTO.fromCartMap(cartService.getCart(req.getUsertId()));
	}

	// DELETE /api/cart/cart-items
	@DeleteMapping("/cart-items")
	public CartDTO remove(@RequestParam Integer userId, @RequestParam Integer productId) {
		cartService.removeItem(userId, productId);
		return CartDTO.fromCartMap(cartService.getCart(userId));
	}

	// DELETE /api/cart?userId=1
	@DeleteMapping
	public CartDTO clear(@RequestParam Integer userId) {
		cartService.clear(userId);
		return CartDTO.fromCartMap(cartService.getCart(userId));
	}
}
