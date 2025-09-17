package com.tibafit.service.cart;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tibafit.model.cart.ProductVO;
import com.tibafit.repository.cart.ProductDAO;

//	新增商品（含 SKU 唯一性檢查）、讀取單筆、分頁列表（支援關鍵字/狀態篩選）。
//	局部更新（只覆寫非 null 欄位，變更 SKU 會再做唯一性檢查）。
//	刪除商品、相對調整庫存。
//	批次依 ID 取回商品清單（給購物車/訂單彙整使用）。

@Service // 商品服務：封裝商品的增刪改查與庫存調整
public class ProductService {
	private final ProductDAO productDAO; // Spring Data JPA Repository

	@Autowired
	public ProductService(ProductDAO productDAO) {
		this.productDAO = productDAO;
	}

	@Transactional // 交易：包含唯一碼檢查與存檔
	public ProductVO create(ProductVO vo) {
		// SKU 唯一性檢查（有帶 productCode 才檢查）
		if (vo.getProductCode() != null && productDAO.findByProductCode(vo.getProductCode()).isPresent())
			throw new IllegalStateException("商品代碼已存在");
		return productDAO.save(vo); // 建立商品
	}

	// 讀單筆商品，找不到丟出業務例外（由 GlobalExceptionHandler 統一轉 400）
	public ProductVO get(Integer id) {
		return productDAO.findById(id).orElseThrow(() -> new IllegalStateException("找不到商品 ID " + id));
	}

	/**
	 * 商品列表（分頁） 查詢優先順序： 1) 先看 keyword（名稱模糊、忽略大小寫） 2) 再看 status（上/下架） 3) 否則回全部
	 * 排序：productId DESC（新到舊）
	 */
	public Page<ProductVO> list(Integer status, String keyword, int page, int size) {
		Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1),
				Sort.by(Sort.Direction.DESC, "productId"));
		if (keyword != null && !keyword.isBlank())
			return productDAO.findByProductNameContainingIgnoreCase(keyword, pageable);
		if (status != null)
			return productDAO.findByProductStatus(status, pageable);
		return productDAO.findAll(pageable);
	}

	/**
	 * 局部更新（只覆寫非 null 欄位） - 若修改了 productCode，仍需檢查唯一性
	 */
	@Transactional
	public ProductVO update(Integer id, ProductVO data) {
		ProductVO db = get(id); // 先抓出資料庫內原物件（受管狀態）
		// SKU 改變時做唯一性檢查
		if (data.getProductCode() != null && !data.getProductCode().equals(db.getProductCode())
				&& productDAO.findByProductCode(data.getProductCode()).isPresent())
			throw new IllegalStateException("商品代碼已存在");
		// 逐欄位部分更新（null 代表不變更）
		if (data.getProductType() != null)
			db.setProductType(data.getProductType());
		if (data.getProductName() != null)
			db.setProductName(data.getProductName());
		if (data.getProductDescription() != null)
			db.setProductDescription(data.getProductDescription());
		if (data.getProductPrice() != null)
			db.setProductPrice(data.getProductPrice());
		if (data.getStockQuantity() != null)
			db.setStockQuantity(data.getStockQuantity());
		if (data.getProductPicture() != null)
			db.setProductPicture(data.getProductPicture());
		if (data.getProductStatus() != null)
			db.setProductStatus(data.getProductStatus());
		if (data.getProductCode() != null)
			db.setProductCode(data.getProductCode());
		return db; // 交易結束時自動 flush
	}

	/** 刪除商品（先檢查存在性） */
	@Transactional
	public void delete(Integer id) {
		if (!productDAO.existsById(id))
			throw new IllegalStateException("商品不存在");
		productDAO.deleteById(id);
	}

	/**
	 * 調整庫存（相對變更）
	 * 
	 * @param delta 可正可負；結果不可 < 0
	 */
	@Transactional
	public ProductVO adjustStock(Integer id, int delta) {
		ProductVO p = get(id);
		int next = (p.getStockQuantity() == null ? 0 : p.getStockQuantity()) + delta;
		if (next < 0)
			throw new IllegalStateException("庫存不足");
		p.setStockQuantity(next);
		return p;
	}

	/** 批次依 ID 取商品清單（購物車/訂單彙整用） */
	public List<ProductVO> findAllByIds(List<Integer> ids) {
		return productDAO.findByProductIdIn(ids);
	}
}
