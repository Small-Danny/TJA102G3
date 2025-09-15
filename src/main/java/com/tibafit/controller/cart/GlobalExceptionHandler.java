package com.tibafit.controller.cart;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	// 商業邏輯自行丟的錯 - 400
	@ExceptionHandler(IllegalStateException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ProblemDetail bad(IllegalStateException e) {
		ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
		pd.setTitle("Bad Request");
		pd.setDetail(e.getMessage());
		return pd;
	}
	
	// @Valid 驗證失敗（JSON 放 body 的表單） - 400
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleValidation(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation Failed");

        // 整理欄位錯誤 => { "recipientName": "收貨人姓名需 2–40 字", ... }
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe -> {
            // 只保留第一個錯誤訊息，避免覆蓋
            errors.putIfAbsent(fe.getField(), fe.getDefaultMessage());
        });
        pd.setProperty("errors", errors);
        pd.setDetail("請修正欄位後再送出");
        return pd;
    }

    // 參數驗證失敗（例如 @PathVariable/@RequestParam 上的驗證） - 400
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleConstraintViolation(jakarta.validation.ConstraintViolationException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation Failed");

        Map<String, String> errors = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(v -> {
            // propertyPath 可能是 like "create.userId"
            String field = v.getPropertyPath() == null ? "param" : v.getPropertyPath().toString();
            errors.put(field, v.getMessage());
        });
        pd.setProperty("errors", errors);
        pd.setDetail("請修正欄位後再送出");
        return pd;
    }

    // JSON 解析錯誤（格式錯/型別錯） - 400
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail badJson(org.springframework.http.converter.HttpMessageNotReadableException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Bad Request");
        pd.setDetail("請確認送出的資料格式正確");
        return pd;
    }
    
	// 其他未攔到的例外 - 500
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ProblemDetail others(Exception e) {
		ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
		pd.setTitle("Server Error");
		pd.setDetail("Server error");
		return pd;
	}
}
