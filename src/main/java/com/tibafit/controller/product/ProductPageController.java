package com.tibafit.controller.product;

import java.math.BigDecimal;
import java.util.*;

import com.tibafit.model.cart.ProductVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.tibafit.service.product.ProductService;

@Controller
@RequestMapping("/shop")
public class ProductPageController {

    private final ProductService psvc;

    public ProductPageController(ProductService psvc) {
        this.psvc = psvc;
    }

    @GetMapping("/products")
    public String list(Model model, @RequestParam(required = false) String q) {
        if (q != null && !q.isBlank()) {
            model.addAttribute("products", psvc.searchCollapsed(q));
        } else {
            model.addAttribute("products", psvc.getAllCollapsed());
        }
        return "frontend/pages/productlist";
    }

    /** 商品詳情（支援 ?size=） */
    @GetMapping("/product/{id}")
    public String detail(@PathVariable Integer id,
                         @RequestParam(name = "size", required = false) String sizeQry,
                         Model model) {

        ProductVO p = psvc.getOne(id);
        if (p == null) return "redirect:/shop/products";

        model.addAttribute("product", p);

        // 依商品型別決定沒單位的 fallback 單位（衣服 null、容器 ML、蛋白粉 G）
        String fallbackUnit = psvc.defaultUnitFor(p);

        // 蒐集同款所有變體，做成 size -> productId、size -> sku 的 map
        Map<String, Integer> sizeMap = new LinkedHashMap<>();
        Map<String, String> sizeSkuMap = new LinkedHashMap<>();
        Map<String, Integer> sizePriceMap = new LinkedHashMap<>();
        
        // 先把「同款」變體都整理進來
        List<ProductVO> variants = psvc.findSizeVariantsByCode(p.getProductCode());
        for (ProductVO v : variants) {
            String sz = psvc.sizeOf(v.getProductCode(), psvc.defaultUnitFor(v)); // 數值尺寸優先，缺單位補上
            if (sz == null) continue;
            String key = sz.trim().toUpperCase(); // key 用大寫作比對
            sizeMap.putIfAbsent(key, v.getProductId());
            sizeSkuMap.putIfAbsent(key, v.getProductCode());
            sizePriceMap.putIfAbsent(key, v.getProductPrice());
        }

        // 確保「自己」也在 map（避免只有本體沒顯示）
        String selfSize = psvc.sizeOf(p.getProductCode(), fallbackUnit);
        if (selfSize != null) {
            String key = selfSize.trim().toUpperCase();
            sizeMap.putIfAbsent(key, p.getProductId());
            sizeSkuMap.putIfAbsent(key, p.getProductCode());
            sizePriceMap.putIfAbsent(key, p.getProductPrice());
        }

        // 若完全沒有尺寸 → 視為「均碼」
        if (sizeMap.isEmpty()) {
            sizeMap.put("均碼", p.getProductId());
            sizeSkuMap.put("均碼", p.getProductCode());
            sizePriceMap.put("均碼", p.getProductPrice());
        }

        // 排序尺寸（S/M/L/XL 與 500ML/1L/250G/2KG 都 OK）
        List<String> sizesSorted = psvc.sortSizes(sizeMap.keySet());

        // 依序重建 LinkedHashMap（保序）
        Map<String, Integer> sizeMapOrdered = new LinkedHashMap<>();
        Map<String, String>  sizeSkuOrdered = new LinkedHashMap<>();
        Map<String, Integer> sizePriceOrdered = new LinkedHashMap<>();
        
        for (String s : sizesSorted) {
            Integer pid = sizeMap.get(s);
            String  sku = sizeSkuMap.get(s);
            
            if (pid != null) sizeMapOrdered.put(s, pid);
            if (sku != null) sizeSkuOrdered.put(s, sku);
            if (sizePriceMap.containsKey(s)) sizePriceOrdered.put(s, sizePriceMap.get(s));
        }
        sizeMap = sizeMapOrdered;
        sizeSkuMap = sizeSkuOrdered;
        sizePriceMap = sizePriceOrdered;

        // 決定目前尺寸（?size > 自身尺寸 > 第一個）
        String currentSize = resolveCurrentSizeWithFallback(p, sizeQry, sizeMap, fallbackUnit);

        // 目前尺寸對應的 SKU
        String currentSku = sizeSkuMap.getOrDefault(currentSize, p.getProductCode());

        // 給前端
        model.addAttribute("sizes", sizesSorted);       // 迭代用
        model.addAttribute("sizeMap", sizeMap);         // th:attr data-pid 用
        model.addAttribute("sizeSkuMap", sizeSkuMap);   // th:attr data-sku 用
        model.addAttribute("currentSize", currentSize); // 用於 chip active 與提示
        model.addAttribute("currentSku", currentSku);   // 初始顯示的 SKU（會被前端 JS動態更新）
        model.addAttribute("sizePriceMap", sizePriceMap); 
        model.addAttribute("currentPrice", sizePriceMap.getOrDefault(currentSize, p.getProductPrice()));
        
        return "frontend/pages/productdetail";
    }

    /** 供庫存查詢 */
    @GetMapping("/api/stock")
    @ResponseBody
    public Map<String, Object> stock(@RequestParam("productId") Integer productId) {
        ProductVO p = psvc.getOne(productId);
        if (p == null) {
            return Map.of("ok", false, "msg", "not found");
        }
        return Map.of("ok", true, "stock", p.getStockQuantity());
    }

    /** 以 fallback 單位輔助決定目前尺寸 */
    private String resolveCurrentSizeWithFallback(ProductVO product,
                                                  String sizeQry,
                                                  Map<String, Integer> sizeToId,
                                                  String fallbackUnit) {
        if (sizeToId == null || sizeToId.isEmpty()) return "均碼";

        // 1) URL ?size=
        if (sizeQry != null) {
            String key = sizeQry.trim().toUpperCase();
            if (sizeToId.containsKey(key)) return key;
        }

        // 2) 自身尺寸（帶 fallback）
        String self = psvc.sizeOf(product.getProductCode(), fallbackUnit);
        if (self != null) {
            String key = self.trim().toUpperCase();
            if (sizeToId.containsKey(key)) return key;
        }

        // 3) 第一個
        return sizeToId.keySet().iterator().next();
    }
}
