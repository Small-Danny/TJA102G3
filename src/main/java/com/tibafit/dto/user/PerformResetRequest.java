package com.tibafit.dto.user;

import lombok.Data;
@Data
public class PerformResetRequest {
    private String token;
    private String newPassword;
    private String confirmPassword;
}
