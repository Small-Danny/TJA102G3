package com.tibafit.controller.user; // It's better practice to put this in a general controller package

import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.imageio.ImageIO;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Controller
public class CaptchaController {

    @Autowired
    private Producer captchaProducer;
    
    @GetMapping("/api/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg");
        
        // 產生驗證碼文字
        String text = captchaProducer.createText();

        // 【核心】將驗證碼文字存入 Session
        request.getSession().setAttribute("captchaCode", text);

        // 產生驗證碼圖片
        BufferedImage image = captchaProducer.createImage(text);

        // 將圖片寫回給前端
        try (ServletOutputStream out = response.getOutputStream()) {
            ImageIO.write(image, "jpg", out);
        }
    }
}