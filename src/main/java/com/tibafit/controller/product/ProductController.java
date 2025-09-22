package com.tibafit.controller.product;

import com.tibafit.model.cart.ProductVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tibafit.service.product.ProductService;

@Controller
@RequestMapping("/admin/products")
public class ProductController {

    private final ProductService svc;

    public ProductController(ProductService svc) {
        this.svc = svc;
    }

    // ===== 新 RESTful 版本 =====

    // 列出所有商品（對應：templates/admin/listAllProduct.html）
    @GetMapping
    public String list(Model model) {
        model.addAttribute("list", svc.getAll());
        return "admin/listAllProduct";
    }

    // 選擇商品頁（對應：templates/admin/select_page.html）
    @GetMapping("/select")
    public String select(Model model) {
        model.addAttribute("list", svc.getAll());
        return "admin/select_page";
    }

    // 單筆查詢（對應：templates/admin/listOneProduct.html）
    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        var product = svc.getOne(id);
        if (product == null) {
            return "redirect:/admin/products";
        }
        model.addAttribute("productVO", product);
        model.addAttribute("product", product);
        return "admin/listOneProduct";
    }

    // 搜尋（回到列表頁顯示查詢結果）
    @GetMapping("/search")
    public String search(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("list", svc.search(q));
        model.addAttribute("q", q); // 若你的頁面要回填關鍵字，可用 ${q}
        return "admin/listAllProduct";
    }

    // 新增商品表單（對應：templates/admin/add_product.html）
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("productVO", new ProductVO());
        return "admin/add_product";
    }

    // 新增商品 (處理提交)
    @PostMapping
    public String insert(ProductVO form, RedirectAttributes ra) {
        svc.add(form);
        ra.addFlashAttribute("successMsg", "新增成功");
        return "redirect:/admin/products";
    }

    // 進入更新表單（對應：templates/admin/update_product_input.html）
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        var product = svc.getOne(id);
        if (product == null) {
            return "redirect:/admin/products";
        }
        model.addAttribute("productVO", product);
        model.addAttribute("product", product);
        return "admin/update_product_input";
    }

    // 更新商品 (處理提交)
    @PostMapping("/{id}/edit")
    public String update(@PathVariable Integer id, ProductVO form, RedirectAttributes ra) {
        form.setProductId(id);
        svc.update(form);
        ra.addFlashAttribute("successMsg", "修改成功");
        return "redirect:/admin/products/" + id;
    }

    // 刪除商品
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        svc.delete(id);
        ra.addFlashAttribute("successMsg", "已刪除");
        return "redirect:/admin/products";
    }
    
    @GetMapping("/select/by-id")
    public String selectById(@RequestParam Integer productId) {
        if (productId == null) {
            return "redirect:/admin/products/select"; // 沒選就回選擇頁
        }
        return "redirect:/admin/products/" + productId; // 導到詳情
    }

}
