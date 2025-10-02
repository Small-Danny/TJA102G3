package com.tibafit.repository.product;

import com.tibafit.model.cart.ProductVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductVO, Integer> {

    // 依代碼找
    @Query("SELECT p FROM ProductVO p WHERE p.productCode = :code")
    Optional<ProductVO> findByCode(@Param("code") String code);

    // 依類型找並照 id 排序
    @Query("SELECT p FROM ProductVO p WHERE p.productType = :type ORDER BY p.productId")
    List<ProductVO> findByTypeOrderById(@Param("type") Integer type);

    // 全部（照 id 排序）— 用 JPQL，不用 Sort
    @Query("SELECT p FROM ProductVO p ORDER BY p.productId")
    List<ProductVO> findAllOrderById();
    
    // 找商品尺寸
    @Query("SELECT p FROM ProductVO p WHERE p.productCode LIKE CONCAT(:prefix, '%')")
    List<ProductVO> findByCodeStartingWith(@Param("prefix") String prefix);
    
    // 依同款代碼找上架中的變體（尺寸）
    List<ProductVO> findByProductCodeAndProductStatusOrderByProductIdAsc(String productCode, int productStatus);

    // 指定 productId 但限定要上架（庫存/詳情防呆）
    Optional<ProductVO> findByProductIdAndProductStatus(Integer productId, int productStatus);

    // 查庫存時也只允許上架的（若庫存存在於 ProductVO）
    @Query("select p.stockQuantity from ProductVO p where p.productId = :pid and p.productStatus = 1")
    Optional<Integer> findStockIfActive(@Param("pid") Integer pid);

    // 關鍵字搜尋（允許空字串 → 全部）
    @Query("""
    		   SELECT p FROM ProductVO p
    		   WHERE (:kw IS NULL OR :kw = '' OR
    		          LOWER(p.productName) LIKE LOWER(CONCAT('%', :kw, '%')) OR
    		          LOWER(p.productCode) LIKE LOWER(CONCAT('%', :kw, '%')) OR
    		          LOWER(p.productDescription) LIKE LOWER(CONCAT('%', :kw, '%')))
    		   ORDER BY p.productId
    		""")
    List<ProductVO> searchByKeywordOrderById(@Param("kw") String kw);
    
	
	 /** 列出同群組所有尺寸/規格（商品詳情頁用） */
	 @Query(value = """
	     WITH base AS (
	         SELECT TRIM(
	             REGEXP_REPLACE(p.product_name,
	               ' (S|M|L|XL)號$| 500ML$| 700ML$| 500G$| 1KG$| 均碼$','')
	         ) AS base_name
	         FROM product p
	         WHERE p.product_id = :productId
	     )
	     SELECT
	         p2.product_id        AS productId,
	         TRIM(TRAILING '號' FROM
	             REGEXP_SUBSTR(p2.product_name, '(S|M|L|XL)(?=號$)|500ML|700ML|500G|1KG|均碼')
	         )                    AS size,
	         p2.stock_quantity    AS stock,
	         p2.product_price     AS price
	     FROM product p2
	     JOIN base b ON TRIM(
	           REGEXP_REPLACE(p2.product_name,
	             ' (S|M|L|XL)號$| 500ML$| 700ML$| 500G$| 1KG$| 均碼$','')
	         ) = b.base_name
	     ORDER BY
	         FIELD(
	           TRIM(TRAILING '號' FROM REGEXP_SUBSTR(p2.product_name,'(S|M|L|XL)(?=號$)|500ML|700ML|500G|1KG|均碼')),
	           'S','M','L','XL','均碼','500ML','700ML','500G','1KG'
	         ),
	         p2.product_id
	     """, nativeQuery = true)
	 List<ProductVariantRow> findVariantsByProductId(@Param("productId") Integer productId);
	
	 /** 由「當前商品 + 尺寸字串」解出對應兄弟 productId */
	 @Query(value = """
	     WITH base AS (
	         SELECT TRIM(
	             REGEXP_REPLACE(p.product_name,
	               ' (S|M|L|XL)號$| 500ML$| 700ML$| 500G$| 1KG$| 均碼$','')
	         ) AS base_name
	         FROM product p
	         WHERE p.product_id = :productId
	     )
	     SELECT p2.product_id
	     FROM product p2
	     JOIN base b ON TRIM(
	           REGEXP_REPLACE(p2.product_name,
	             ' (S|M|L|XL)號$| 500ML$| 700ML$| 500G$| 1KG$| 均碼$','')
	         ) = b.base_name
	     WHERE TRIM(TRAILING '號' FROM
	             REGEXP_SUBSTR(p2.product_name,'(S|M|L|XL)(?=號$)|500ML|700ML|500G|1KG|均碼')
	         ) = :size
	     LIMIT 1
	     """, nativeQuery = true)
	 Optional<Integer> findSiblingProductIdBySize(@Param("productId") Integer productId,
	                                              @Param("size") String size);
	
	// 依 product_code 的「基底」找同款所有尺寸（例如 EQ-C- 開頭）
	    @Query("select p from ProductVO p where p.productCode like concat(:base,'%')")
	    List<ProductVO> findByProductCodeStartingWith(@Param("base") String base);
	    
	    /** 批次更新上/下架狀態 */
	    @Modifying
	    @Query("update ProductVO p set p.productStatus = :status where p.productId in :ids")
	    int bulkUpdateStatus(@Param("ids") List<Integer> ids, @Param("status") int status);
}
