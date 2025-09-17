package com.tibafit.repository.cart;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tibafit.model.cart.OrderItemVO;

public interface OrderItemDAO extends JpaRepository<OrderItemVO, Integer> {
	
  // NEW
  List<OrderItemVO> findByOrder_OrderId(Integer orderId);
  
}
