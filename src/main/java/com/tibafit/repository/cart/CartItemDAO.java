package com.tibafit.repository.cart;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tibafit.model.cart.CartItemVO;

public interface CartItemDAO extends JpaRepository<CartItemVO, Integer> {
	
  List<CartItemVO> findByUserId(Integer userId);                        
  
  Optional<CartItemVO> findByUserIdAndProductId(Integer userId, Integer productId);  
  
  void deleteByUserId(Integer userId);
  
  void deleteByUserIdAndProductId(Integer userId, Integer productId);
  
  boolean existsByUserIdAndProductId(Integer userId, Integer productId);             
}
