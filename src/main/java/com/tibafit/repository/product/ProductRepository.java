package com.tibafit.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tibafit.model.product.ProductVO;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductVO, Integer> {

    // 依代碼找
    @Query("SELECT p FROM ProductVO p WHERE p.product_code = :code")
    Optional<ProductVO> findByCode(@Param("code") String code);

    // 依類型找並照 id 排序
    @Query("SELECT p FROM ProductVO p WHERE p.product_type = :type ORDER BY p.product_id")
    List<ProductVO> findByTypeOrderById(@Param("type") Integer type);

    // 全部（照 id 排序）— 用 JPQL，不用 Sort
    @Query("SELECT p FROM ProductVO p ORDER BY p.product_id")
    List<ProductVO> findAllOrderById();

    // 關鍵字搜尋（允許空字串 → 全部）
    @Query("""
           SELECT p FROM ProductVO p
           WHERE (:kw IS NULL OR :kw = '' OR
                  LOWER(p.product_name) LIKE LOWER(CONCAT('%', :kw, '%')) OR
                  LOWER(p.product_description) LIKE LOWER(CONCAT('%', :kw, '%')))
           ORDER BY p.product_id
           """)
    List<ProductVO> searchByKeywordOrderById(@Param("kw") String kw);
}
