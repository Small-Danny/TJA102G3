package com.tibafit.service.sport;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tibafit.dto.sport.UploadFileResponseDTO;
import com.tibafit.model.sporttype.UploadFileFuncType;

@Service
public class UploadFileService implements UploadFileService_interface {
	
    @Value("${file.upload-sport-dir}")
    private String UPLOAD_SPORT_DIR_PATH;

	/*
	 * 上傳檔案
	 */
	@Override
    public UploadFileResponseDTO uploadPic(UploadFileFuncType funcType, MultipartFile file) {
    	try {
			if(file == null) {
				throw new IllegalArgumentException("檔案為null");
			}
			
	        // 副檔名檢查
	        String originalFilename = file.getOriginalFilename();
	        if (originalFilename == null || !originalFilename.contains(".")) {
	            throw new IllegalArgumentException("檔案缺少副檔名");
	        }
	        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
	        if (!(extension.equals(".jpg") || extension.equals(".jpeg") || extension.equals(".png"))) {
	            throw new IllegalArgumentException("僅允許上傳 JPG/JPEG/PNG 圖片");
	        }
	
	        // 實際檔案類型檢查
	        String contentType = file.getContentType();
	        if (contentType == null || 
	            !("image/jpeg".equalsIgnoreCase(contentType) || "image/jpg".equalsIgnoreCase(contentType) || "image/png".equalsIgnoreCase(contentType))) {
	            throw new IllegalArgumentException("檔案內容不是合法的 JPG/PNG 圖片");
	        }
	
	        // 生成 hash 當檔名
	        String hash = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
	        String fileName = hash + extension;
	        
	        // 目錄 = 基本路徑 + enum 對應名稱
	        String targetDir = UPLOAD_SPORT_DIR_PATH + File.separator + funcType;
	        Files.createDirectories(Paths.get(targetDir));  // IOException
	
	        // 寫入檔案
	        File dest = new File(targetDir, fileName);
	        file.transferTo(dest);  // IOException
	        
	        UploadFileResponseDTO resDto = new UploadFileResponseDTO();
	        resDto.setFileName(fileName);
	        resDto.setFolder(funcType);
	        String webPath = "/sportPics/img/" + funcType + "/" + fileName;
	        resDto.setWebPath(webPath);
	
	        return resDto;
    	} catch (IOException e) {
    		throw new RuntimeException("UploadFileService uploadPic error: ", e);
    	}
    }
    
    
    /**
     * 刪除檔案
     */
	@Override
    public boolean deleteFile(UploadFileFuncType funcType, String fileName) {
		try {
	        if (fileName == null || fileName.isEmpty()) {
	            return false;
	        }
	        String targetDir = UPLOAD_SPORT_DIR_PATH + File.separator + funcType;
	        File targetFile = new File(targetDir, fileName);
	        if (targetFile.exists()) {
	            boolean deleteResult = targetFile.delete();
	            if (!deleteResult) {
	                System.err.println("刪除失敗：" + targetFile.getAbsolutePath());
	            }
	            return deleteResult;
	        } else {
	            return false;
	        }
		} catch (SecurityException e) {
			throw new RuntimeException("UploadFileService deleteFile error: ", e);
		}
    }

    
    /**
     * 更新檔案
     */
	@Override
	public UploadFileResponseDTO updateFile(UploadFileFuncType funcType, String oldFileName, MultipartFile newFile) {
		try {
		    // 有新檔 : 先上傳，再刪舊檔
		    if (newFile != null && !newFile.isEmpty()) {
		        UploadFileResponseDTO resDto = uploadPic(funcType, newFile);
	
		        if (oldFileName != null && !oldFileName.isEmpty()) {
		            deleteFile(funcType, oldFileName);
		        }
	
		        return resDto;
		    }
	
		    // 沒有新檔 : 移除舊圖
		    if (oldFileName != null && !oldFileName.isEmpty()) {
		        deleteFile(funcType, oldFileName);
		    }
	
		    // 回傳「沒有圖」，null
		    return null;
		} catch (RuntimeException e) {
			throw new RuntimeException("UploadFileService updateFile error: ", e);
		}
	}
	
	
	/**
	 * 取得單一檔案資訊
	 */
	@Override
	public UploadFileResponseDTO getFile(UploadFileFuncType funcType, String fileName) {
	    if (fileName == null || fileName.isEmpty()) {
	        return null;
	    }
	    String targetDir = UPLOAD_SPORT_DIR_PATH + File.separator + funcType;
	    File targetFile = new File(targetDir, fileName);

	    if (!targetFile.exists()) {
	        return null; // 找不到檔案
	    }

	    UploadFileResponseDTO resDto = new UploadFileResponseDTO();
	    resDto.setFileName(fileName);
	    resDto.setFolder(funcType);
	    String webPath = "/sportPics/img/" + funcType + "/" + fileName;
	    resDto.setWebPath(webPath);

	    return resDto;
	}

	/**
	 * 取得功能下所有檔案資訊
	 */
	@Override
	public Map<String, UploadFileResponseDTO> listFileMap(UploadFileFuncType funcType) {
	    String targetDir = UPLOAD_SPORT_DIR_PATH + File.separator + funcType;
	    File dir = new File(targetDir);
	    Map<String, UploadFileResponseDTO> fileMap = new HashMap<>();

	    if (dir.exists() && dir.isDirectory()) {
	        File[] files = dir.listFiles();
	        if (files != null) {
	            for (File f : files) {
	                if (f.isFile()) {
	                    UploadFileResponseDTO dto = new UploadFileResponseDTO();
	                    dto.setFileName(f.getName());
	                    dto.setFolder(funcType);
	                    dto.setWebPath("/sportPics/img/" + funcType + "/" + f.getName());
	                    
	                    fileMap.put(f.getName(), dto); // key 先用檔名
	                }
	            }
	        }
	    }
	    return fileMap;
	}
}
