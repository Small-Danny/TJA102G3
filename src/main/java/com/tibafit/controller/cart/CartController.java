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

//	讀取購物車、加入商品、設定數量、移除單品、清空整車（全部走 Redis）。
//	提供 /api/cart/{userId}/summary 給「訂單頁右側清單」使用，回傳每項明細與總數量、總金額。
//	資料來源由 CartService（Redis）與 ProductService（查商品資訊）共同完成，回前端統一使用 CartDTO 或 map 結構顯示。

@RestController // 這是一支 REST 控制器，方法回傳值會自動序列化為 JSON
@RequestMapping("/api/cart") // 本控制器所有 API 的共同前綴
public class CartController {

	private final CartService cartService; // 操作 Redis 購物車（Hash：cart:{userId} -> {productId: qty}）
	private final ProductService productService; // 查商品資訊（名稱、價格、狀態等）

	@Autowired // 建構子注入：Spring 會把 Bean 傳進來
	public CartController(CartService cartService, ProductService productService) {
		this.cartService = cartService;
		this.productService = productService;
	}

	// ✅ 訂單頁右側「購物清單摘要」會用到
	@GetMapping("/{userId}/summary") // GET /api/cart/{userId}/summary
	public Map<String, Object> getSummary(@PathVariable Integer userId) {
		// 1) 從 Redis 取得購物車（Map<Object,Object> 是因為 RedisTemplate 預設用 Object 泛型）
		Map<Object, Object> cart = cartService.getCart(userId);

		// 空車就回空資料結構，避免前端再判斷 null
		if (cart == null || cart.isEmpty()) {
			Map<String, Object> resp = new LinkedHashMap<>();
			resp.put("items", List.of());
			resp.put("totalQuantity", 0);
			resp.put("totalAmount", 0);
			return resp;
		}

		// 2) 撈商品資訊（一次把購物車內所有 productId 查出來）
		List<Integer> ids = cart.keySet().stream().map(k -> Integer.valueOf(k.toString())).toList();

		// 依你現有的 ProductService 命名來呼叫；若是 JPA 預設可用 findAllById(ids)
		List<ProductVO> products = productService.findAllByIds(ids);

		// 轉 map 方便用 productId 取回 ProductVO
		Map<Integer, ProductVO> pmap = products.stream()
				.collect(Collectors.toMap(ProductVO::getProductId, Function.identity()));

		// 3) 組 items 並計算總數量/總金額
		List<Map<String, Object>> items = new ArrayList<>();
		int totalQty = 0;
		int totalAmt = 0;

		for (var e : cart.entrySet()) {
			Integer pid = Integer.valueOf(e.getKey().toString());
			Integer qty = (Integer) e.getValue();
			if (qty == null || qty <= 0)
				continue; // 0 或負數當作不存在

			ProductVO p = pmap.get(pid);
			if (p == null)
				continue; // 商品可能下架或不存在，直接略過

			int price = p.getProductPrice(); // 依你的欄位命名取單價
			int sub = price * qty;

			// 每一列明細要回給前端的字段
			Map<String, Object> line = new LinkedHashMap<>();
			line.put("productId", pid);
			line.put("productName", p.getProductName());
			line.put("unitPrice", price);
			line.put("quantity", qty);
			line.put("subtotal", sub);

			items.add(line);
			totalQty += qty;
			totalAmt += sub;
		}

		// 最後包成 summary 回前端
		Map<String, Object> resp = new LinkedHashMap<>();
		resp.put("items", items);
		resp.put("totalQuantity", totalQty);
		resp.put("totalAmount", totalAmt);
		return resp;
	}

	// 讀購物車（前台頁面載入時呼叫）
	// 例：GET /api/cart/cart-items?userId=1
	@GetMapping("/cart-items")
	public CartDTO get(@RequestParam Integer userId) {
		// 從 Redis 取回 Hash → 轉為陣列 DTO（包含 totalQuantity）
		return CartDTO.fromCartMap(cartService.getCart(userId));
	}

	// 新增（或加入）某商品到購物車
	// 例：POST /api/cart/items { userId, productId, qty }
	@PostMapping("/items")
	public CartDTO add(@RequestBody @Valid CartAddItemDTO req) {
		cartService.addItem(req.getUserId(), req.getProductId(), req.getQty());
		return CartDTO.fromCartMap(cartService.getCart(req.getUserId())); // 回最新購物車
	}

	// 設定某商品數量（idempotent）
	// 例：PUT /api/cart/items { userId, productId, qty }
	@PutMapping("/items")
	public CartDTO set(@RequestBody @Valid CartSetQuantityDTO req) {
		// ⚠︎ 原始程式為 getUsertId()（多一個 t），會編譯失敗；請確保 DTO 是 getUserId()
		cartService.setQuantity(req.getUserId(), req.getProductId(), req.getQty());
		return CartDTO.fromCartMap(cartService.getCart(req.getUserId()));
	}

	// 移除某項商品
	// 例：DELETE /api/cart/cart-items?userId=1&productId=201
	@DeleteMapping("/cart-items")
	public CartDTO remove(@RequestParam Integer userId, @RequestParam Integer productId) {
		cartService.removeItem(userId, productId);
		return CartDTO.fromCartMap(cartService.getCart(userId));
	}

	// 清空整車
	// 例：DELETE /api/cart?userId=1
	@DeleteMapping
	public CartDTO clear(@RequestParam Integer userId) {
		cartService.clear(userId);
		return CartDTO.fromCartMap(cartService.getCart(userId));
	}
}
