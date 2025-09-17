package com.tibafit.controller.cart;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tibafit.model.cart.ProductVO;
import com.tibafit.service.cart.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // GET /api/products?ids=1,2,3
    @GetMapping
    public List<ProductVO> listProducts(@RequestParam String ids){
        List<Integer> idList = Arrays.stream(ids.split(","))
                .map(Integer::valueOf)
                .toList();
        return productService.findAllByIds(idList);
    }
}