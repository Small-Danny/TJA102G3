package com.tibafit.controller.cart;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//	IllegalStateException：業務邏輯錯誤 → 400
//	MethodArgumentNotValidException：@Valid 驗證 body 失敗 → 400（附 errors 欄位）
//	ConstraintViolationException：參數驗證失敗 → 400（附 errors 欄位）
//	HttpMessageNotReadableException：JSON 解析/型別錯誤 → 400
//	其他未攔到的例外 → 500
//	可維持 API 錯誤輸出的一致性、可預測性，也避免把內部堆疊與細節洩漏給使用者

@RestControllerAdvice // 全域例外處理：攔截所有 @RestController 的未處理例外並統一回應格式
public class GlobalExceptionHandlers {

	// === 業務邏輯主動拋出的錯誤（例如：庫存不足、購物車為空等）→ HTTP 400 ===
	@ExceptionHandler(IllegalStateException.class) // 指定要攔截的例外類型
	@ResponseStatus(HttpStatus.BAD_REQUEST) // 統一回傳 400 Bad Request
	public ProblemDetail bad(IllegalStateException e) {
		ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST); // 建立 RFC 7807 標準問題描述
		pd.setTitle("Bad Request"); // 短標題（前端可直接顯示）
		pd.setDetail(e.getMessage()); // 詳細錯誤訊息（從例外內文帶出）
		return pd; // 回傳後會自動序列化為 JSON
	}

	// === @Valid 驗證失敗（JSON 放在 body 的表單）→ HTTP 400 ===
	@ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ProblemDetail handleValidation(org.springframework.web.bind.MethodArgumentNotValidException ex) {
		ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
		pd.setTitle("Validation Failed"); // 驗證失敗標題

		// 整理欄位錯誤為一個 Map，方便前端逐欄位顯示：
		// 例如：{ "recipientName": "收貨人姓名需 2–40 字", "recipientPhone": "格式不正確" }
		Map<String, String> errors = new LinkedHashMap<>(); // LinkedHashMap 保持插入順序，呈現更穩定
		ex.getBindingResult().getFieldErrors().forEach(fe -> {
			// 若同一欄位有多個錯誤，只保留第一個，避免覆蓋彼此訊息
			errors.putIfAbsent(fe.getField(), fe.getDefaultMessage());
		});
		pd.setProperty("errors", errors); // 透過 ProblemDetail 的 extension 欄位放自訂資料
		pd.setDetail("請修正欄位後再送出"); // 統一的詳細指引
		return pd;
	}

	// === 參數驗證失敗（多發生於 @PathVariable / @RequestParam 的約束）→ HTTP 400 ===
	@ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ProblemDetail handleConstraintViolation(jakarta.validation.ConstraintViolationException ex) {
		ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
		pd.setTitle("Validation Failed");

		Map<String, String> errors = new LinkedHashMap<>();
		ex.getConstraintViolations().forEach(v -> {
			// propertyPath 例："create.userId" 或 "userId"；前端可視需要做 key 轉換
			String field = v.getPropertyPath() == null ? "param" : v.getPropertyPath().toString();
			errors.put(field, v.getMessage());
		});
		pd.setProperty("errors", errors);
		pd.setDetail("請修正欄位後再送出");
		return pd;
	}

	// === JSON 無法解析（格式錯誤 / 型別錯誤 / 缺值導致反序列化失敗）→ HTTP 400 ===
	@ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ProblemDetail badJson(org.springframework.http.converter.HttpMessageNotReadableException ex) {
		ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
		pd.setTitle("Bad Request");
		pd.setDetail("請確認送出的資料格式正確"); // 不洩漏過多內部細節，給出一致的人性化提示
		return pd;
	}

	// === 其他未捕捉的例外（系統錯誤）→ HTTP 500 ===
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ProblemDetail others(Exception e) {
		ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
		pd.setTitle("Server Error"); // 統一標題，避免把內部堆疊直接曝露到前端
		pd.setDetail("Server error"); // 可依環境（dev/prod）決定是否顯示 e.getMessage()
		return pd;
	}
}
