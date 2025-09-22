package com.tibafit.dto.cart; // 注意 package 路徑，如果不同請自行修改

import lombok.Data;
import java.util.List;

@Data
public class LinePayRequest {
    private Integer amount;
    private String currency = "TWD";
    private String orderId;
    private List<Package> packages;
    private RedirectUrls redirectUrls;


    @Data
    public static class Package {
        private String id;
        private Integer amount;
        private List<Product> products;
    }

    @Data
    public static class Product {
        private String name;
        private Integer quantity;
        private Integer price;
    }

    @Data
    public static class RedirectUrls {
        private String confirmUrl;
        private String cancelUrl;
    }
}