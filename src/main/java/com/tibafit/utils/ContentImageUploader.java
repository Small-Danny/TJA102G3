package com.tibafit.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

public class ContentImageUploader {

    private final Cloudinary cloudinary;

    public ContentImageUploader(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /** 將文章內的 Base64 圖片上傳到 Cloudinary 並替換 URL */
    public String uploadContentImages(String htmlContent) throws IOException {
        Document doc = Jsoup.parse(htmlContent);
        Elements images = doc.select("img");

        for (Element img : images) {
            String src = img.attr("src");
            if (src.startsWith("data:image")) {
                String base64Data = src.substring(src.indexOf(",") + 1);
                byte[] bytes = Base64.getDecoder().decode(base64Data);

                Map uploadResult = cloudinary.uploader()
                        .upload(bytes, ObjectUtils.asMap("folder", "forum/content"));
                String url = (String) uploadResult.get("secure_url");
                img.attr("src", url);
            }
        }
        return doc.body().html();
    }
}
