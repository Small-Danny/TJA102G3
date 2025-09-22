package com.tibafit.dto.cart; // 或 com.tibafit.dto.linepay

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LinePayResponse {
    private String returnCode;
    private String returnMessage;
    private Info info;

    @Data
    // ★★★ 把這個註解也加到內部的 Info 類別上 ★★★
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Info {
        private PaymentUrl paymentUrl;
        private Long transactionId;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true) // 順便也幫最小的內部類別加上
        public static class PaymentUrl {
            private String web;
            private String app;
        }

        public String getWebPaymentUrl() {
            return (paymentUrl != null) ? paymentUrl.getWeb() : null;
        }
    }
}