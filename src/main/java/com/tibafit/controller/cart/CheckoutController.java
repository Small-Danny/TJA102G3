package com.tibafit.controller.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tibafit.dto.cart.CheckoutCreateDTO;
import com.tibafit.dto.cart.OrdersDTO;
import com.tibafit.model.cart.OrdersVO;
import com.tibafit.service.cart.CheckoutService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {
	private final CheckoutService checkoutService;
	
	@Autowired
	public CheckoutController(CheckoutService checkoutService) {
	    this.checkoutService = checkoutService;
	  }

	@PostMapping
	public OrdersDTO create(@RequestBody @Valid CheckoutCreateDTO req) {
		OrdersVO ov = checkoutService.createOrderFromCart(
				req.getUserId(),
				req.getRecipientName().trim(),
                req.getRecipientPhone().trim(),
                req.getRecipientAddress().trim(),
                req.getUsedPoints() == null ? 0 : req.getUsedPoints()
				);
		
		return OrdersDTO.from(ov);
	}
}
