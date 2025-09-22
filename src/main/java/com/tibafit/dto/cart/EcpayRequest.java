package com.tibafit.dto.cart;

import lombok.Data;

import java.util.Map;
import java.util.TreeMap;

@Data
public class EcpayRequest {
    private String MerchantID;
    private String MerchantTradeNo;
    private String MerchantTradeDate;
    private String PaymentType = "aio";
    private String TotalAmount;
    private String TradeDesc;
    private String ItemName;
    private String ReturnURL;
    private String ChoosePayment = "Credit";
    private String CheckMacValue;
    private String EncryptType = "1";


    // 這是一個方便的方法，可以將所有屬性轉成一個 Map，以便後續計算 CheckMacValue
    public Map<String, String> toMap() {
        Map<String, String> map = new TreeMap<>(); // TreeMap會自動按Key排序
        map.put("MerchantID", this.MerchantID);
        map.put("MerchantTradeNo", this.MerchantTradeNo);
        map.put("MerchantTradeDate", this.MerchantTradeDate);
        map.put("PaymentType", this.PaymentType);
        map.put("TotalAmount", this.TotalAmount);
        map.put("TradeDesc", this.TradeDesc);
        map.put("ItemName", this.ItemName);
        map.put("ReturnURL", this.ReturnURL);
        map.put("ChoosePayment", this.ChoosePayment);
        map.put("EncryptType", this.EncryptType);
        return map;
    }
}