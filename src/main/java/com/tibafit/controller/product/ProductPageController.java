package com.tibafit.controller.product;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tibafit.service.product.ProductService;

@Controller
@RequestMapping("/shop")
public class ProductPageController {

	private final ProductService psvc;
	public ProductPageController(ProductService psvc) {
		this.psvc = psvc;
	}
	
	@GetMapping("/products")
    public String list(Model model, @RequestParam(required=false) String q) {
        if (q != null && !q.isBlank())
            model.addAttribute("products", psvc.search(q));
        else
            model.addAttribute("products", psvc.getAll());
        return "frontend/pages/productlist"; //  productlist.html
    }

    @GetMapping("/product/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        model.addAttribute("product", psvc.getOne(id));
        return "frontend/pages/productdetail"; // productdetail.html
    }
}
