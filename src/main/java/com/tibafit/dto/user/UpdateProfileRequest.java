package com.tibafit.dto.user;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String name; // 新增這個屬性
    private String nickName;
    private Integer gender;
    private String phone;
    private BigDecimal heightCm;
    private BigDecimal weightKg;
    private String profilePicture;
}