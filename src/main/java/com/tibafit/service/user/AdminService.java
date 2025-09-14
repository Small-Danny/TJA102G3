package com.tibafit.service.user;

import com.tibafit.dto.user.ChangePasswordRequest;

public interface AdminService {
    void changeAdminPassword(String currentAdminAccount, ChangePasswordRequest request);

}
