package com.tibafit.repository.cart;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tibafit.model.cart.ProductsVO;

//	findOnShelfPrice(id)：只在商品「上架」時回傳單價（無需撈整筆資料）。
//	decreaseStock(id, qty)：原子性扣庫存（條件：庫存足夠且上架），回傳 0/1 表示是否成功。
//	findByProductCode(...)：以 SKU 尋找商品。
//	findByProductNameContainingIgnoreCase(...)、findByProductStatus(...)：後台列表常用的條件＋分頁。
//	findByProductIdIn(ids)：購物車/訂單一次撈多筆商品資料。

public interface ProductDAO extends JpaRepository<ProductsVO, Integer> {

	// 取得「上架商品」的單價（只選 price 欄位，不撈整筆）
	// 回傳：Integer（可能為 null：當商品不存在或已下架）
	@Query("select p.productPrice from ProductVO p where p.productId=:id and p.productStatus=1")
	Integer findOnShelfPrice(@Param("id") Integer id);

	// 扣庫存：僅在「庫存足夠且商品上架」時成功
	// 回傳：受影響筆數（1 代表成功扣到，0 代表失敗：可能庫存不足或商品下架）
	// ⚠ 需在 Service 層或方法上加上 @Transactional 才會生效（@Modifying 不會自動開交易）
	@Modifying
	@Query("update ProductVO p set p.stockQuantity=p.stockQuantity-:qty "
			+ "where p.productId=:id and p.stockQuantity>=:qty and p.productStatus=1")
	int decreaseStock(@Param("id") Integer id, @Param("qty") Integer qty);

	// 依商品代碼（SKU）查找單筆
	Optional<ProductsVO> findByProductCode(String productCode);

	// 依商品名稱「模糊」（忽略大小寫）分頁查詢
	Page<ProductsVO> findByProductNameContainingIgnoreCase(String keyword, Pageable pageable);

	// 依商品狀態分頁查詢（例如 1=上架, 0=下架）
	Page<ProductsVO> findByProductStatus(Integer status, Pageable pageable);

	// 批次依 ID 清單查詢（常用於購物車/訂單一次撈多筆）
	List<ProductsVO> findByProductIdIn(List<Integer> ids);	  
}