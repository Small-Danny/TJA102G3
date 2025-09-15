package com.tibafit.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tibafit.dto.cart.CartSetQuantityDTO;
import com.tibafit.model.cart.CartItemVO;
import com.tibafit.repository.cart.CartItemDAO;
import com.tibafit.service.cart.CartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/carts")
public class CartAdminController {
	private final CartService cartService;
	private final CartItemDAO cartItemDAO;

	@Autowired
	public CartAdminController(CartService cartService, CartItemDAO cartItemDAO) {
		this.cartService = cartService;
		this.cartItemDAO = cartItemDAO;
	}

	@GetMapping("/{userId}")
	public List<CartItemVO> list(@PathVariable Integer userId) {
		return cartItemDAO.findByUserId(userId);
	}

	@PostMapping("/{userId}/items")
	public void add(@PathVariable Integer userId, @RequestBody @Valid CartSetQuantityDTO req) {
		cartService.setQuantity(userId, req.getProductId(), req.getQty());
	}

	@PutMapping("/{userId}/items")
	public void setQty(@PathVariable Integer userId, @RequestBody @Valid CartSetQuantityDTO req) {
		cartService.setQuantity(userId, req.getProductId(), req.getQty());
	}

	@DeleteMapping("/{userId}/items/{productId}")
	public void remove(@PathVariable Integer userId, @PathVariable Integer productId) {
		cartService.removeItem(userId, productId);
	}

	@DeleteMapping("/{userId}")
	public void clear(@PathVariable Integer userId) {
		cartService.clear(userId);
	}
}
