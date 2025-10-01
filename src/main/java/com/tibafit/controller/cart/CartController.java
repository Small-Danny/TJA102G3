package com.tibafit.controller.cart;

import com.tibafit.dto.cart.CartAddItemDTO;
import com.tibafit.dto.cart.CartDTO;
import com.tibafit.dto.cart.CartSetQuantityDTO;
import com.tibafit.model.cart.ProductVO;
import com.tibafit.model.user.User;
import com.tibafit.repository.user.UserRepository;
import com.tibafit.service.cart.CartService;
import com.tibafit.service.cart.ProductServices;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	private final CartService cartService;
	private final ProductServices productServices;
	private final UserRepository userRepository;

	@Autowired
	// ★★★ 修正 #1：建構子移除 CartIdService 的注入 ★★★
	public CartController(CartService cartService, ProductServices productServices, UserRepository userRepository) {
		this.cartService = cartService;
		this.productServices = productServices;
		this.userRepository = userRepository;
	}

	// ★★★ 修正 #2：新增一個 private 方法來取代 CartIdService ★★★
	/**
	 * 獲取當前購物車的唯一識別碼。 - 如果使用者已登入，返回 user ID 的字串形式。 - 如果使用者是訪客，返回其 session ID。
	 */
	private String getCartId(Authentication authentication, HttpSession session) {
		if (authentication != null && authentication.isAuthenticated()
				&& !"anonymousUser".equals(authentication.getPrincipal())) {
			// 已登入：使用 email 去資料庫反查 userId，這才是最可靠的方式
			String userEmail = authentication.getName();
			User currentUser = userRepository.findByEmail(userEmail)
					.orElseThrow(() -> new UsernameNotFoundException("在 CartController 中找不到使用者: " + userEmail));
			return currentUser.getUserId().toString();
		}
		// 未登入：使用 session ID
		return session.getId();
	}

	// ✅ API-1: 取得購物車摘要
	@GetMapping("/summary")
	public Map<String, Object> getSummary(Authentication authentication, HttpSession session) {
		// ★★★ 修正 #3：所有 API 方法都改為呼叫這個內部的 getCartId 方法 ★★★
		String cartId = getCartId(authentication, session);
		Map<Object, Object> cart = cartService.getCart(cartId);

		if (cart == null || cart.isEmpty()) {
			Map<String, Object> resp = new LinkedHashMap<>();
			resp.put("items", List.of());
			resp.put("totalQuantity", 0);
			resp.put("totalAmount", 0);
			return resp;
		}

		List<Integer> ids = cart.keySet().stream().map(k -> Integer.valueOf(k.toString())).toList();
		List<ProductVO> products = productServices.findAllByIds(ids);
		Map<Integer, ProductVO> pmap = products.stream()
				.collect(Collectors.toMap(ProductVO::getProductId, Function.identity()));

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

			int price = p.getProductPrice();
			int sub = price * qty;

			Map<String, Object> line = new LinkedHashMap<>();
			line.put("productId", pid);
			line.put("productName", p.getProductName());
			line.put("productPicture", p.getProductPicture());
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

	// ✅ API-2: 新增商品
	@PostMapping("/items")
	public CartDTO add(@RequestBody @Valid CartAddItemDTO req, Authentication authentication, HttpSession session) {
		String cartId = getCartId(authentication, session);
		cartService.addItem(cartId, req.getProductId(), req.getQty());
		return CartDTO.fromCartMap(cartService.getCart(cartId));
	}

	// ✅ API-3: 修改數量
	@PutMapping("/items")
	public CartDTO set(@RequestBody @Valid CartSetQuantityDTO req, Authentication authentication, HttpSession session) {
		String cartId = getCartId(authentication, session);
		cartService.setQuantity(cartId, req.getProductId(), req.getQty());
		return CartDTO.fromCartMap(cartService.getCart(cartId));
	}

	// ✅ API-4: 移除單項商品
	@DeleteMapping("/items")
	public ResponseEntity<CartDTO> remove(@RequestParam Integer productId, Authentication authentication,
			HttpSession session) {
		String cartId = getCartId(authentication, session);
		cartService.removeItem(cartId, productId);
		CartDTO dto = CartDTO.fromCartMap(cartService.getCart(cartId));
		return ResponseEntity.ok(dto);
	}

	// ✅ API-5: 清空購物車
	@DeleteMapping
	public CartDTO clear(Authentication authentication, HttpSession session) {
		String cartId = getCartId(authentication, session);
		cartService.clear(cartId);
		return CartDTO.fromCartMap(cartService.getCart(cartId));
	}
}