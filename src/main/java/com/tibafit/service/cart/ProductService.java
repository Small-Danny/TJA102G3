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

@Service
public class ProductService {
  private final ProductDAO productDAO;
  
  @Autowired
  public ProductService(ProductDAO productDAO) {
      this.productDAO = productDAO;
  }
  
  @Transactional
  public ProductVO create(ProductVO vo){
    if (vo.getProductCode()!=null && productDAO.findByProductCode(vo.getProductCode()).isPresent())
      throw new IllegalStateException("商品代碼已存在");
    return productDAO.save(vo);
  }

  public ProductVO get(Integer id){
    return productDAO.findById(id).orElseThrow(() -> new IllegalStateException("找不到商品 ID " + id));
  }

  public Page<ProductVO> list(Integer status, String keyword, int page, int size){
    Pageable pageable = PageRequest.of(Math.max(page,0), Math.max(size,1), Sort.by(Sort.Direction.DESC, "productId"));
    if (keyword!=null && !keyword.isBlank()) return productDAO.findByProductNameContainingIgnoreCase(keyword, pageable);
    if (status!=null) return productDAO.findByProductStatus(status, pageable);
    return productDAO.findAll(pageable);
  }

  @Transactional
  public ProductVO update(Integer id, ProductVO data){
    ProductVO db = get(id);
    if (data.getProductCode()!=null && !data.getProductCode().equals(db.getProductCode())
        && productDAO.findByProductCode(data.getProductCode()).isPresent())
      throw new IllegalStateException("商品代碼已存在");
    if (data.getProductType()!=null) db.setProductType(data.getProductType());
    if (data.getProductName()!=null) db.setProductName(data.getProductName());
    if (data.getProductDescription()!=null) db.setProductDescription(data.getProductDescription());
    if (data.getProductPrice()!=null) db.setProductPrice(data.getProductPrice());
    if (data.getStockQuantity()!=null) db.setStockQuantity(data.getStockQuantity());
    if (data.getProductPicture()!=null) db.setProductPicture(data.getProductPicture());
    if (data.getProductStatus()!=null) db.setProductStatus(data.getProductStatus());
    if (data.getProductCode()!=null) db.setProductCode(data.getProductCode());
    return db;
  }

  @Transactional
  public void delete(Integer id){
    if (!productDAO.existsById(id)) throw new IllegalStateException("商品不存在");
    productDAO.deleteById(id);
  }

  @Transactional
  public ProductVO adjustStock(Integer id, int delta){
    ProductVO p = get(id);
    int next = (p.getStockQuantity()==null?0:p.getStockQuantity()) + delta;
    if (next < 0) throw new IllegalStateException("庫存不足");
    p.setStockQuantity(next);
    return p;
  }
  
  public List<ProductVO> findAllByIds(List<Integer> ids){
      return productDAO.findByProductIdIn(ids);
  }
}
