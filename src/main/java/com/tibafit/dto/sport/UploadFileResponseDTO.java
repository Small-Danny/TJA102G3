package com.tibafit.dto.sport;

import com.tibafit.model.sporttype.UploadFileFuncType;

public class UploadFileResponseDTO {
	    private String fileName;
	    private UploadFileFuncType folder;
	    private String webPath;
	    
	    public UploadFileResponseDTO() {
	    	// 空建構子
	    }

	    public UploadFileResponseDTO(String fileName, UploadFileFuncType folder, String webPath) {
	        this.fileName = fileName;
	        this.folder = folder;
	        this.webPath = webPath;
	    }

	    
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		
		public UploadFileFuncType getFolder() {
			return folder;
		}
		public void setFolder(UploadFileFuncType folder) {
			this.folder = folder;
		}

		
		public String getWebPath() {
			return webPath;
		}
		public void setWebPath(String webPath) {
			this.webPath = webPath;
		}


	}
