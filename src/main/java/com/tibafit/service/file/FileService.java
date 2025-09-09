package com.tibafit.service.file;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
	public abstract String saveFile(MultipartFile file)throws IOException;
	public abstract void deleteFile(String oldFileName);
	 // 新增一個可以指定子目錄的方法
    String saveFile(MultipartFile file, String subDir) throws IOException;

}
