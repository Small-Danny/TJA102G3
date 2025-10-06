package com.tibafit.service.sport;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.tibafit.dto.sport.UploadFileResponseDTO;
import com.tibafit.model.sporttype.UploadFileFuncType;

public interface UploadFileService_interface {
	
	public UploadFileResponseDTO uploadPic(UploadFileFuncType funcType, MultipartFile file);
	
	public boolean deleteFile(UploadFileFuncType funcType, String fileName);
	
	public UploadFileResponseDTO updateFile(UploadFileFuncType funcType, String oldFileName, MultipartFile newFile);
	
	public UploadFileResponseDTO getFile(UploadFileFuncType funcType, String fileName);
	
	public Map<String, UploadFileResponseDTO> listFileMap(UploadFileFuncType funcType);
}
