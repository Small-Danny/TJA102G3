package com.tibafit.service.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tibafit.model.cart.OrdersVO;
import com.tibafit.repository.cart.OrdersDAO;
import com.tibafit.repository.cart.ProductDAO;

@Service
public class PaymentService {
	private final CheckoutService checkoutService;
	private final CartService cartService;

	@Autowired
	public PaymentService(CheckoutService checkoutService, CartService cartService) {
		this.checkoutService = checkoutService;
		this.cartService = cartService;
	}

	public OrdersVO mockPay(Integer userId, Integer orderId, boolean success) {
		try {
			Thread.sleep(800);
		} catch (Exception ignored) {
		}

		if (success) {
			var o = checkoutService.markPaid(orderId);
			cartService.clear(userId);
			return o;
		}
		return checkoutService.markFailed(orderId);
	}
}
