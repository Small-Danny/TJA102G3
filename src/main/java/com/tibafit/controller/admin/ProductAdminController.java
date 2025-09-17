package com.tibafit.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tibafit.dto.product.ProductCreateDTO;
import com.tibafit.dto.product.ProductDTO;
import com.tibafit.dto.product.ProductUpdateDTO;
import com.tibafit.model.cart.ProductVO;
import com.tibafit.repository.cart.ProductDAO;
import com.tibafit.service.cart.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/products")
public class ProductAdminController {
  private final ProductService productService;
  
  @Autowired
  public ProductAdminController(ProductService productService) {
      this.productService = productService;
  }

  @PostMapping
  public ProductDTO create(@RequestBody @Valid ProductCreateDTO req){
    ProductVO vo = new ProductVO();
    vo.setProductType(req.getProductType());
    vo.setProductName(req.getProductName());
    vo.setProductDescription(req.getProductDescription());
    vo.setProductPrice(req.getProductPrice().intValue());
    vo.setStockQuantity(req.getStockQuantity());
    vo.setProductPicture(req.getProductPicture());
    vo.setProductStatus(req.getProductStatus());
    vo.setProductCode(req.getProductCode());
    return ProductDTO.from(productService.create(vo));
  }

  @GetMapping("/{id}")
  public ProductDTO get(@PathVariable Integer id){ return ProductDTO.from(productService.get(id)); }

  @GetMapping
  public Page<ProductDTO> list(@RequestParam(required=false) Integer status,
                               @RequestParam(required=false) String keyword,
                               @RequestParam(defaultValue="0") int page,
                               @RequestParam(defaultValue="10") int size){
    return productService.list(status, keyword, page, size).map(ProductDTO::from);
  }

  @PutMapping("/{id}")
  public ProductDTO update(@PathVariable Integer id, @RequestBody @Valid ProductUpdateDTO req){
    ProductVO data = new ProductVO();
    data.setProductType(req.getProductType());
    data.setProductName(req.getProductName());
    data.setProductDescription(req.getProductDescription());
    data.setProductPrice(req.getProductPrice().intValue());
    data.setStockQuantity(req.getStockQuantity());
    data.setProductPicture(req.getProductPicture());
    data.setProductStatus(req.getProductStatus());
    data.setProductCode(req.getProductCode());
    return ProductDTO.from(productService.update(id, data));
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Integer id){ productService.delete(id); }

  @PatchMapping("/{id}/stock")
  public ProductDTO adjustStock(@PathVariable Integer id, @RequestParam int delta){
    return ProductDTO.from(productService.adjustStock(id, delta));
  }
}
