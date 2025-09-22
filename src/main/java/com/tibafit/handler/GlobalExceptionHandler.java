package com.tibafit.handler; // 統一放在這個通用的 package

import com.tibafit.exception.ValidationException; // 引入你的自訂例外
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import jakarta.validation.ConstraintViolationException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// 處理自訂的 ValidationException
	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ProblemDetail handleCustomValidation(ValidationException ex) {
		ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
		pd.setTitle("Validation Failed");
		pd.setDetail(ex.getMessage());
		pd.setProperty("errors", Map.of(ex.getField(), ex.getMessage()));
		return pd;
	}

	// 處理業務邏輯的 IllegalStateException
	@ExceptionHandler(IllegalStateException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ProblemDetail handleIllegalState(IllegalStateException e) {
		ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
		pd.setTitle("Bad Request");
		pd.setDetail(e.getMessage());
		return pd;
	}

	// 處理 @Valid 驗證失敗
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
		ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
		pd.setTitle("Validation Failed");
		Map<String, String> errors = new LinkedHashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(fe -> {
			errors.putIfAbsent(fe.getField(), fe.getDefaultMessage());
		});
		pd.setProperty("errors", errors);
		pd.setDetail("請修正欄位後再送出");
		return pd;
	}

	// 處理參數驗證失敗
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
		ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
		pd.setTitle("Validation Failed");
		Map<String, String> errors = new LinkedHashMap<>();
		ex.getConstraintViolations().forEach(v -> {
			String field = v.getPropertyPath() == null ? "param" : v.getPropertyPath().toString();
			errors.put(field, v.getMessage());
		});
		pd.setProperty("errors", errors);
		pd.setDetail("請修正欄位後再送出");
		return pd;
	}

	// 處理 JSON 解析錯誤
	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ProblemDetail handleHttpBodyNotReadable(HttpMessageNotReadableException ex) {
		ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
		pd.setTitle("Bad Request");
		pd.setDetail("請確認送出的資料格式或型別正確");
		return pd;
	}

	// 處理所有其他未捕獲的例外
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ProblemDetail handleAllOtherExceptions(Exception e) {
		ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
		pd.setTitle("Server Error");
		pd.setDetail("系統發生未預期錯誤，請稍後再試");
		e.printStackTrace(); // 在後台印出詳細錯誤堆疊，方便追查問題
		return pd;
	}
}