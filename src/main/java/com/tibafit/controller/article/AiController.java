package com.tibafit.controller.article;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/ai")
public class AiController {

	@Value("${ollama.api.key}")
	private String apiKey;

	@PostMapping("/optimizeArticle")
	// 步驟1: 接收文章內容 接收前端給的JSON 裡面有"content"文章內容
//	public ResponseEntity<Map<String, Object>> optimizeArticle(@RequestBody Map<String, String> body) {
	public ResponseEntity<Map<String, Object>> optimizeArticle(@RequestBody Map<String, Object> body) {
		String content = (String)body.get("content");
		Map<String, Object> result = new HashMap<>();

		// 檢查是否回空 如果沒有內容就回傳錯誤
		if (content == null || content.trim().isEmpty()) {
			result.put("message", "文章內容為空");
			return ResponseEntity.badRequest().body(result);
		}

		// 從前端接收參數，給預設值
		double temperature = body.get("temperature") != null ? ((Number) body.get("temperature")).doubleValue() : 0.7;
		String model = body.get("model") != null ? body.get("model").toString() : "gpt-oss:120b-cloud";
		
		// 步驟2: 處理文章內容
		// ex:這是文章<img src="a.jpg">繼續文章 變成:這是文章[[IMG_1]]繼續文章
		Pattern imgPattern = Pattern.compile("<img[^>]*src=[\"']([^\"']+)[\"'][^>]*>");
		Matcher matcher = imgPattern.matcher(content);

		Map<String, String> imgMap = new HashMap<>();
		int index = 1;
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String imgTag = matcher.group(0);
			String imgSrc = matcher.group(1);
			String placeholder = "[[IMG_" + index + "]]";
			imgMap.put(placeholder, imgTag); // 保留原始 img 標籤
			matcher.appendReplacement(sb, placeholder);
			index++;
		}
		matcher.appendTail(sb);
		String contentForAI = sb.toString();

		try {
			HttpClient client = HttpClient.newHttpClient();

			// 步驟3:用 HttpClient 建立一個 HTTP POST 請求，送到 https://ollama.com/api/chat。
			Map<String, Object> requestBody = new HashMap<>();
//			requestBody.put("model", "gpt-oss:120b-cloud");
			requestBody.put("model", model);
			requestBody.put("messages",
					List.of(Map.of("role", "user", "content", "請幫我不足20字時寫成300字文章,300字以上的話幫我優化文章\n" + contentForAI)));
//			requestBody.put("temperature", 0.7);
			requestBody.put("temperature",temperature);
			requestBody.put("stream", false);
			ObjectMapper mapper = new ObjectMapper();

			// 輸出結果{"model": "gpt-oss:120b-cloud","messages": [{"role": "user","content":
			// "請幫我不足20字時寫成300字文章,300字以上的話幫我優化文章\n文章內容..."}],"temperature": 0.7,"stream":
			// false}

			String json = mapper.writeValueAsString(requestBody);

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://ollama.com/api/chat"))
					.header("Content-Type", "application/json").header("Authorization", "Bearer " + apiKey)
					.POST(HttpRequest.BodyPublishers.ofString(json)).build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			System.out.println("API 請求 JSON: " + json);
			System.out.println("API 回應狀態碼: " + response.statusCode());
			System.out.println("API 回應內容: " + response.body());

			if (response.statusCode() == 200) {
				Map<String, Object> respMap = mapper.readValue(response.body(), Map.class);

				// 步驟4: 解析 AI 回傳內容
				Object msgObj = respMap.get("message");
				if (msgObj instanceof Map<?, ?> message) {
					Object contentObj = message.get("content");
					if (contentObj != null) {
						String optimized = contentObj.toString();

						// 步驟5: 回填圖片
						for (Map.Entry<String, String> entry : imgMap.entrySet()) {
							optimized = optimized.replace(entry.getKey(), entry.getValue());
						}

						result.put("optimizedContent", optimized);
						return ResponseEntity.ok(result);
					}
				}

				result.put("message", "AI 沒有回傳內容");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);

			} else {
				result.put("message", "AI 優化失敗: " + response.body());
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("message", "AI 優化發生錯誤: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
		}
	}
}
