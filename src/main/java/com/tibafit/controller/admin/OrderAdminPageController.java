package com.tibafit.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tibafit.model.cart.OrdersVO;
import com.tibafit.service.cart.OrdersService;


@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class OrderAdminPageController {
	
	private final OrdersService ordersService;

    public OrderAdminPageController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @GetMapping("/orders")
    public String ordersPage(@RequestParam(required = false) Integer userId,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "50") int size,
                             Model model) {
        Page<OrdersVO> p = ordersService.list(userId, page, size);
        model.addAttribute("orders", p.getContent());   // ← 給 th:each 用
        model.addAttribute("pageTotal", p.getTotalPages());
        model.addAttribute("pageNo", p.getNumber());
        return "admin/orders";                          // 對應 templates/admin/orders.html
    }
}