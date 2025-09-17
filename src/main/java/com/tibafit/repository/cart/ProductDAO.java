package com.tibafit.repository.cart;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tibafit.model.cart.ProductVO;

public interface ProductDAO extends JpaRepository<ProductVO, Integer>{
	
	@Query("select p.productPrice from ProductVO p where p.productId=:id and p.productStatus=1")
	  Integer findOnShelfPrice(@Param("id") Integer id);
	
	@Modifying
	@Query("update ProductVO p set p.stockQuantity=p.stockQuantity-:qty " +
	       "where p.productId=:id and p.stockQuantity>=:qty and p.productStatus=1")
	  int decreaseStock(@Param("id") Integer id, @Param("qty") Integer qty);
	
	  // NEW
	  Optional<ProductVO> findByProductCode(String productCode);
	  // NEW
	  Page<ProductVO> findByProductNameContainingIgnoreCase(String keyword, Pageable pageable);
	  // NEW
	  Page<ProductVO> findByProductStatus(Integer status, Pageable pageable);
	  
	  List<ProductVO> findByProductIdIn(List<Integer> ids);
	  
	  
}
