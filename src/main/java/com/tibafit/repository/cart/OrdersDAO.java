package com.tibafit.repository.cart;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tibafit.model.cart.OrdersVO;

public interface OrdersDAO extends JpaRepository<OrdersVO, Integer>{
	
	  // NEW
	  Page<OrdersVO> findByUserIdOrderByOrderDateDesc(Integer userId, Pageable pageable);
	  // NEW
	  boolean existsByOrderCode(String orderCode);
	  
}
