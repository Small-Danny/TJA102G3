// OrderController.java
package com.tibafit.controller.cart;

import com.tibafit.dto.cart.OrdersDTO;
import com.tibafit.model.user.User;
import com.tibafit.repository.user.UserRepository;
import com.tibafit.service.cart.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrdersService ordersService;
    private final UserRepository userRepository;

    @Autowired
    public OrderController(OrdersService ordersService, UserRepository userRepository) {
        this.ordersService = ordersService;
        this.userRepository = userRepository;
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrdersDTO>> getMyOrders(Authentication authentication) {
        // 1. 【安全】從 Spring Security 取得當前登入者資訊
        String userEmail = authentication.getName();
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("找不到使用者: " + userEmail));
        Integer userId = currentUser.getUserId();

        // 2. 呼叫 Service 取得該使用者的所有訂單
        List<OrdersDTO> orders = ordersService.findOrdersByUserId(userId);

        // 3. 回傳 200 OK 和訂單列表的 JSON
        return ResponseEntity.ok(orders);
    }
}