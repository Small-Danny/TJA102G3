package com.tibafit.service.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tibafit.exception.ValidationException;

@Service
public class FileServiceImpl implements FileService {

	@Value("${file.upload-dir}")
	private String baseUploadDir;
	
	/**
	 * 儲存上傳的檔案到指定的子目錄。
	 *
	 * 這個方法會生成一個唯一的檔名，並將檔案儲存在基礎目錄下的指定子目錄中。
	 * 如果子目錄不存在，會自動創建。
	 *
	 * @param file 接收到的檔案物件，來自前端的 multipart/form-data 請求。
	 * @param subDir 要儲存檔案的子目錄名稱。例如："avatars"、"products" 等。
	 * 如果為空或 null，檔案將儲存在根目錄。
	 * @return 儲存成功後，返回包含子目錄和新檔名的完整相對路徑。
	 * 例如：如果 subDir 是 "avatars"，會返回 "avatars/a1b2c3d4e5.jpg"。
	 * @throws IOException 如果在儲存檔案過程中發生 IO 錯誤。
	 * @throws ValidationException 如果上傳的檔案為空。
	 */
	@Override
	public String saveFile(MultipartFile file, String subDir) throws IOException {
		if (file == null || file.isEmpty()) {
			throw new ValidationException("file", "上傳的檔案不可為空");
		}

		Path uploadPath = Paths.get(baseUploadDir, subDir);
		if (Files.notExists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}
		if (!Files.isWritable(uploadPath)) {
			throw new IOException("上傳目錄沒有寫入權限: " + uploadPath.toString());
		}

		String originalFilename = file.getOriginalFilename();
		if (originalFilename == null || originalFilename.trim().isEmpty()) {
			throw new ValidationException("fileName", "上傳的檔案沒有原始檔名");
		}

		// 提取文件擴展名
		String extension = "";
		int dotIndex = originalFilename.lastIndexOf('.');
		if (dotIndex >= 0) {
			extension = originalFilename.substring(dotIndex);
		}

		// 生成唯一文件名
		String newFileName = UUID.randomUUID().toString() + extension;
		Path destinationPath = uploadPath.resolve(newFileName);

		try {
			// 使用transferTo()替代Files.copy(),可以不用手動處理輸入流
			file.transferTo(destinationPath);
		} catch (IOException e) {
			throw new IOException("儲存檔案失敗: " + e.getMessage(), e);
		}

	    // 回傳的檔名應該包含子目錄，這樣在刪除時才找得到
	    // 並且前端也可以正確載入圖片
		System.out.println("檔案儲存的根目錄: " + baseUploadDir);
	    return subDir + "/" + newFileName;
	}

	@Override
	public String saveFile(MultipartFile file) throws IOException {
		return saveFile(file, ""); // 預設存到根目錄
	}

	@Override
    public void deleteFile(String oldFileName) {
        if (oldFileName == null || oldFileName.trim().isEmpty()) {
            return; // 只是刪除，如果檔名為空，直接返回即可，不用拋出例外
        }
        
        try {
            Path oldFilePath = Paths.get(baseUploadDir, oldFileName);
            if(Files.exists(oldFilePath) && !Files.isDirectory(oldFilePath)) {
                 Files.delete(oldFilePath);
            }
        } catch (IOException e) {
            // 在刪除失敗時，通常是記錄日誌，而不是讓整個操作失敗
            System.err.println("刪除舊檔案失敗: " + oldFileName + "，錯誤: " + e.getMessage());
        }
    }
}