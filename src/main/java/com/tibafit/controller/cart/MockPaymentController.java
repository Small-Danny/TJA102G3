package com.tibafit.controller.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tibafit.dto.cart.OrdersDTO;
import com.tibafit.dto.cart.PaymentMockDTO;
import com.tibafit.model.cart.OrdersVO;
import com.tibafit.service.cart.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
public class MockPaymentController {
	private final PaymentService paymentService;
	
	@Autowired
	public MockPaymentController(PaymentService paymentService) {
	    this.paymentService = paymentService;
	  }
	
	@PostMapping("/mock")
	public OrdersDTO mock(@RequestBody @Valid PaymentMockDTO req){
	    OrdersVO ov = paymentService.mockPay(
	    		req.getUserId(),
	    		req.getOrderId(),
	    		req.isSuccess());
	    
	    return OrdersDTO.from(ov);
	  }
}
