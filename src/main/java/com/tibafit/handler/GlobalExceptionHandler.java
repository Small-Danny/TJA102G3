package com.tibafit.handler;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tibafit.exception.ValidationException;

//這個 class 是所有 @RestController 的顧問，全域例外處理器,專門處理它們拋出的例外。
@RestControllerAdvice
public class GlobalExceptionHandler {
	// ExceptionHandler這個方法專門處理 IllegalArgumentException 這種例
	@ExceptionHandler(ValidationException.class) // 1. 改成我們自訂的 ValidationException
	public ResponseEntity<Map<String, String>> handleValidationException(ValidationException ex) {

		// 2. 建立包含 field 和 message 的 Map
		Map<String, String> errorResponse = Map.of("field", ex.getField(), "message", ex.getMessage());

		// 3. 回傳 400 狀態碼和這個 Map
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}
}
