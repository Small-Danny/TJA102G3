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
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}