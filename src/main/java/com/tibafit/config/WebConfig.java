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
    
    /**
     * 負責將前端的圖片 URL 映射到後端實際的檔案儲存位置。
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        
        //規則一：專門處理使用者上傳的頭像
        // 當 URL 是 /avatars/** 時，去你設定的實體路徑下的 /avatars/ 子資料夾找檔案
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:" + uploadDir + "/avatars/");

        // 規則二：專門處理專案內的靜態圖片 (例如預設頭像)
        // 當 URL 是 /images/** 時，去專案內部預設的 static/images/ 資料夾找檔案
        // 這是 Spring Boot 的標準作法，可以跟動態上傳的圖片分開處理
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
    }
}