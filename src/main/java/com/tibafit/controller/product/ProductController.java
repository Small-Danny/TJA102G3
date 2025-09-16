package com.tibafit.controller.product;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tibafit.model.product.*;
import com.tibafit.service.product.ProductService;

@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductService svc;

    public ProductController(ProductService svc) {
        this.svc = svc;
    }

    // 直接導到 select_page
    @GetMapping({"", "/"})
    public String index() {
        return "redirect:/product/product.do?action=select_page";
    }

    // ===== GET：查詢/頁面 =====
    @GetMapping("/product.do")
    public String routeGet(
            @RequestParam(value = "action", required = false) String action,
            @RequestParam(value = "product_id", required = false) Integer id,
            @RequestParam(value = "q", required = false) String q,
            Model model) {

        if (action == null || action.isBlank()) {
            return "redirect:/product/product.do?action=select_page";
        }

        switch (action) {
            case "select_page" -> {
                model.addAttribute("list", svc.getAll());
                return "admin/select_page";
            }
            case "getAll" -> {
                model.addAttribute("list", svc.getAll());
                return "admin/listAllProduct";
            }
            case "getOne_For_Display" -> {
                if (id == null) return "redirect:/product/product.do?action=select_page";
                model.addAttribute("productVO", svc.getOne(id));
                return "admin/listOneProduct";
            }
            case "search" -> {
                model.addAttribute("list", svc.search(q));
                return "admin/listAllProduct";
            }
            case "add_form" -> {
                return "admin/add_product";
            }
            default -> {
                return "redirect:/product/product.do?action=select_page";
            }
        }
    }

    // ===== POST：新增/更新/刪除/進入編輯表單/ =====
    @PostMapping("/product.do")
    public String routePost(
            @RequestParam("action") String action,
            @RequestParam(value = "product_id", required = false) Integer id,
            ProductVO form,
            Model model,
            RedirectAttributes ra) {

        switch (action) {
            // --- POST -> GET ---
            case "getAll" -> {
                return "redirect:/product/product.do?action=getAll";
            }
            case "getOne_For_Display" -> {
                if (id == null) return "redirect:/product/product.do?action=select_page";
                return "redirect:/product/product.do?action=getOne_For_Display&product_id=" + id;
            }
            // -----------------------------------

            case "getOne_For_Update" -> {
                if (id == null) return "redirect:/product/product.do?action=getAll";
                model.addAttribute("productVO", svc.getOne(id));
                return "admin/update_product_input";
            }
            case "add_form" -> {
                return "admin/add_product";
            }
            case "insert" -> {
                svc.add(form);
                ra.addFlashAttribute("successMsg", "新增成功");
                return "redirect:/product/product.do?action=getAll";
            }
            case "update" -> {
                svc.update(form);
                ra.addFlashAttribute("successMsg", "修改成功");
                return "redirect:/product/product.do?action=getOne_For_Update&product_id=" + form.getProduct_id();
            }
            case "delete" -> {
                if (id != null) svc.delete(id);
                ra.addFlashAttribute("successMsg", "已刪除");
                return "redirect:/product/product.do?action=getAll";
            }
            default -> {
                return "redirect:/product/product.do?action=select_page";
            }
        }
    }
}
