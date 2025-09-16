package com.tibafit.service.product;

import org.springframework.stereotype.Service;

import com.tibafit.model.product.ProductVO;
import com.tibafit.repository.product.ProductRepository;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public void add(ProductVO v)       { repo.save(v); }
    public void update(ProductVO v)    { repo.save(v); }
    public void delete(Integer id)     { repo.deleteById(id); }

    public ProductVO getOne(Integer id) {
        return repo.findById(id).orElse(null);
    }

    public ProductVO findByCode(String code) {
        return repo.findByCode(code).orElse(null);
    }

    public List<ProductVO> findByType(Integer type) {
        return repo.findByTypeOrderById(type);
    }

    public List<ProductVO> getAll() {
        return repo.findAllOrderById();
    }

    public List<ProductVO> search(String keyword) {
        return repo.searchByKeywordOrderById(keyword);
    }
}
