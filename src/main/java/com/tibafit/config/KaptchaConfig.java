package com.tibafit.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

@Configuration
public class KaptchaConfig {
//免費產生圖片的函式庫
    @Bean
    public DefaultKaptcha producer() {
        Properties properties = new Properties();
        // 圖片邊框
        properties.setProperty("kaptcha.border", "yes");
        properties.setProperty("kaptcha.border.color", "105,179,90");
        // 字體顏色
        properties.setProperty("kaptcha.textproducer.font.color", "blue");
        // 圖片寬高
        properties.setProperty("kaptcha.image.width", "120");
        properties.setProperty("kaptcha.image.height", "46");
        // 字體大小
        properties.setProperty("kaptcha.textproducer.font.size", "32");
        // Session Key
        properties.setProperty("kaptcha.session.key", "captchaCode");
        // 驗證碼長度
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        // 字體
        properties.setProperty("kaptcha.textproducer.font.names", "Arial,Courier");
        
        Config config = new Config(properties);
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}
