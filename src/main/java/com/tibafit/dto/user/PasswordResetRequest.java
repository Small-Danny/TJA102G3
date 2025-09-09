package com.tibafit.dto.user;

import lombok.Data;

@Data
public class PasswordResetRequest {
    private String email;
    private String captcha; 
}