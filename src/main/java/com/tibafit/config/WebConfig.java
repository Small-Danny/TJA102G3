package com.tibafit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	//負責將前端的圖片 URL 映射到後端實際的檔案儲存位置。

	@Value("${file.upload-dir}")
	private String uploadDir;

	@Value("${file.upload-sport-dir}")
	private String uploadSportDir;

	/**
	 * 負責將前端的圖片 URL 映射到後端實際的檔案儲存位置。
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		// 規則零：統一的上傳檔案入口
		String uploadRootUri = toDirUri(java.nio.file.Paths.get(uploadDir));
		String uploadImgUri  = toDirUri(java.nio.file.Paths.get(uploadDir, "frontend-template", "assets", "img"));
		String classpathImg  = "classpath:/static/frontend-template/assets/img/"; // <== 新增這個

		registry.addResourceHandler("/uploads/**")
				.addResourceLocations(uploadRootUri) // 外部根
				.addResourceLocations(uploadImgUri)  // 外部 img 夾
				.addResourceLocations(classpathImg); // 內建靜態（你現在 clothes.png 的位置）

		//規則一：專門處理使用者上傳的頭像
		// 當 URL 是 /avatars/** 時，去你設定的實體路徑下的 /avatars/ 子資料夾找檔案
		registry.addResourceHandler("/avatars/**")
				.addResourceLocations("file:" + uploadDir + "/avatars/");

		// 規則二：專門處理專案內的靜態圖片 (例如預設頭像)
		// 當 URL 是 /images/** 時，去專案內部預設的 static/images/ 資料夾找檔案
		// 這是 Spring Boot 的標準作法，可以跟動態上傳的圖片分開處理
		registry.addResourceHandler("/images/**")
				.addResourceLocations("classpath:/static/images/");


		// 規則三（重點）：商品圖片
		// 1) 先從外部 uploads/frontend-template/assets/img/ 讀
		// 2) 找不到再回落到 classpath:/static/frontend-template/assets/img/
		registry.addResourceHandler("/frontend-template/assets/img/**")
				.addResourceLocations(
						"file:" + normalize(uploadDir) + "frontend-template/assets/img/",
						"classpath:/static/frontend-template/assets/img/"
				);

		// 規則四：其餘前端靜態資源仍走 classpath
		registry.addResourceHandler("/frontend-template/**")
				.addResourceLocations("classpath:/static/frontend-template/");

		// 規則五: 運動計畫通用圖片(公開)
		// fd
		registry.addResourceHandler("/sportPics/publicImg/fd/sportCommon/**")
				.addResourceLocations("file:" + normalize(uploadSportDir) + "/sportCommon/");
		// 運動/自訂義運動/運動分類圖片
		// fd
		registry.addResourceHandler("/sportPics/img/fd/**").addResourceLocations(
				"file:" + normalize(uploadSportDir) + "/");
		// 運動/自訂義運動/運動分類圖片
		// bd
		registry.addResourceHandler("/sportPics/img/bd/**").addResourceLocations(
				"file:" + normalize(uploadSportDir) + "/");
	}


	/** 確保結尾有斜線，避免路徑拼接變成資料夾底下再多一層同名檔案夾的問題 */
	private String normalize(String path) {
		if (path == null || path.isEmpty()) return "";
		return path.endsWith("/") || path.endsWith("\\") ? path : (path + "/");
	}

	private static String toDirUri(java.nio.file.Path dir) {
		String uri = dir.toAbsolutePath().toUri().toString(); // e.g. file:///D:/app-data/.../img/
		return uri.endsWith("/") ? uri : uri + "/";
	}
}