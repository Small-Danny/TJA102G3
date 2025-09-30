package com.tibafit.controller.product;

import com.tibafit.model.cart.ProductVO;
import com.tibafit.service.product.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/admin/products")
public class ProductController {

    private final ProductService svc;

    // 動態路徑
    @Value("${file.upload-dir}")
    private String uploadDir;
    public ProductController(ProductService svc) {
        this.svc = svc;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("list", svc.getAll());
        return "admin/listAllProduct";
    }

    @GetMapping("/select")
    public String select(Model model) {
        model.addAttribute("list", svc.getAll());
        return "admin/select_page";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        var product = svc.getOne(id);
        if (product == null) return "redirect:/admin/products";
        model.addAttribute("productVO", product);
        model.addAttribute("product", product);
        return "admin/listOneProduct";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("list", svc.search(q));
        model.addAttribute("q", q);
        return "admin/listAllProduct";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("productVO", new ProductVO());
        return "admin/add_product";
    }

    /** 新增商品（支援上傳 imageFile） */
    @PostMapping("/add")
    public String insert(
            ProductVO form,
            @RequestParam(name = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes ra
    ) {
        try {
            String savedFileName = saveImageIfPresent(imageFile);
            if (savedFileName != null) {
                // 只存檔名到資料庫
                form.setProductPicture(savedFileName);
            }
            svc.add(form);
            ra.addFlashAttribute("successMsg", "新增成功");
            return "redirect:/admin/products";
        } catch (IOException e) {
            ra.addFlashAttribute("errorMsg", "圖片上傳失敗：" + e.getMessage());
            return "redirect:/admin/products/add";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        var product = svc.getOne(id);
        if (product == null) return "redirect:/admin/products";
        model.addAttribute("productVO", product);
        model.addAttribute("product", product);
        return "admin/update_product_input";
    }

    /** 更新商品（支援上傳 imageFile；未選檔則保留原圖） */
    @PostMapping("/{id}/edit")
    public String update(
            @PathVariable Integer id,
            ProductVO form,
            @RequestParam(name = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes ra
    ) {
        form.setProductId(id);
        try {
            String savedFileName = saveImageIfPresent(imageFile);
            if (savedFileName != null) {
                form.setProductPicture(savedFileName);
            }
            svc.update(form);
            ra.addFlashAttribute("successMsg", "修改成功");
            return "redirect:/admin/products/" + id;
        } catch (IOException e) {
            ra.addFlashAttribute("errorMsg", "圖片上傳失敗：" + e.getMessage());
            return "redirect:/admin/products/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        svc.delete(id);
        ra.addFlashAttribute("successMsg", "已刪除");
        return "redirect:/admin/products";
    }

    @GetMapping("/select/by-id")
    public String selectById(@RequestParam Integer productId) {
        if (productId == null) return "redirect:/admin/products/select";
        return "redirect:/admin/products/" + productId;
    }
    
    // 商品上下架
    @PostMapping("/{id}/status")
    public String switchStatus(@PathVariable Integer id,
                               @RequestParam("status") Integer status,
                               RedirectAttributes ra) {
        ProductVO p = svc.getOne(id);
        if (p == null) {
            ra.addFlashAttribute("successMsg", "找不到商品");
            return "redirect:/admin/products";
        }
        p.setProductStatus((status != null && status == 1) ? 1 : 0);
        svc.update(p);
        ra.addFlashAttribute("successMsg",
                "已將【" + p.getProductName() + "】設為" + (p.getProductStatus() == 1 ? "上架" : "下架"));
        return "redirect:/admin/products";
    }

    // ===================== 工具方法 =====================

    /**
     * 有選檔就存到：${file.upload-dir}/frontend-template/assets/img/
     * 回傳「檔名」（不含路徑）；無檔或非法檔則回傳 null。
     */
    private String saveImageIfPresent(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        // 檢查檔案是否為圖片
        String ct = Optional.ofNullable(file.getContentType()).orElse("").toLowerCase();
        if (!ct.startsWith("image/")) {
            throw new IOException("檔案類型不正確（僅允許圖片）");
        }

        // 建立存放資料夾
        Path imgDir = Paths.get(uploadDir, "frontend-template", "assets", "img");
        Files.createDirectories(imgDir);

        // 取原始檔名（僅檔名部分，避免路徑注入）
        String originalFileName = Optional.ofNullable(file.getOriginalFilename())
                .map(name -> Paths.get(name).getFileName().toString())
                .orElseThrow(() -> new IOException("檔名不存在"));

        // 拆出副檔名與主檔名
        String baseName;
        String ext = "";
        int dot = originalFileName.lastIndexOf('.');
        if (dot != -1) {
            baseName = originalFileName.substring(0, dot);
            ext = originalFileName.substring(dot); // 包含「.」
        } else {
            baseName = originalFileName;
        }

        // 生成最終檔名（若重複就自動加 (1), (2)...）
        String finalFileName = originalFileName;
        Path target = imgDir.resolve(finalFileName);
        int count = 1;
        while (Files.exists(target)) {
            finalFileName = baseName + "(" + count + ")" + ext;
            target = imgDir.resolve(finalFileName);
            count++;
        }

        // 寫入檔案
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target);
        }

        return finalFileName;
    }

}
