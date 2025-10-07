package com.tibafit.controller.sport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.tibafit.dto.sport.ApiResponseDTO;
import com.tibafit.dto.sport.UploadFileResponseDTO;
import com.tibafit.model.sporttype.UploadFileFuncType;
import com.tibafit.service.sport.UploadFileService_interface;

import java.util.Map;

@RestController
@RequestMapping("/fileImg/api/fd")
public class UploadFileFdController {

    @Autowired
    private UploadFileService_interface uploadFileService;

    // 上傳檔案
    @PostMapping("/upload")
    public ApiResponseDTO<UploadFileResponseDTO> uploadFile(
            @RequestParam("funcType") UploadFileFuncType funcType,
            @RequestParam("file") MultipartFile file) {
        UploadFileResponseDTO res = uploadFileService.uploadPic(funcType, file);
        return ApiResponseDTO.success(res);
    }

    // 刪除檔案
    @PostMapping("/delete")
    public ApiResponseDTO<Boolean> deleteFile(
            @RequestParam("funcType") UploadFileFuncType funcType,
            @RequestParam("fileName") String fileName) {
        boolean result = uploadFileService.deleteFile(funcType, fileName);
        return ApiResponseDTO.success(result);
    }

    // 更新檔案
    @PostMapping("/update")
    public ApiResponseDTO<UploadFileResponseDTO> updateFile(
            @RequestParam("funcType") UploadFileFuncType funcType,
            @RequestParam(value = "oldFileName", required = false) String oldFileName,
            @RequestParam(value = "newFile", required = false) MultipartFile newFile) {
        UploadFileResponseDTO res = uploadFileService.updateFile(funcType, oldFileName, newFile);
        return ApiResponseDTO.success(res);
    }

    // 取得單一檔案資訊
    @GetMapping("/getSingle")
    public ApiResponseDTO<UploadFileResponseDTO> getSingle(
            @RequestParam("funcType") UploadFileFuncType funcType,
            @RequestParam("fileName") String fileName) {
        UploadFileResponseDTO res = uploadFileService.getFile(funcType, fileName);
        return ApiResponseDTO.success(res);
    }

    // 取得該功能下的所有檔案
    @GetMapping("/getMultipleByFunc")
    public ApiResponseDTO<Map<String, UploadFileResponseDTO>> getMultipleByFunc(
            @RequestParam("funcType") UploadFileFuncType funcType) {
        Map<String, UploadFileResponseDTO> fileMap = uploadFileService.listFileMap(funcType);
        return ApiResponseDTO.success(fileMap);
    }
}
