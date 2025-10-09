package com.tibafit.controller.sport;

import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tibafit.dto.sport.ApiResponseDTO;

@Order(1)
@RestControllerAdvice(basePackages = {
	    "com.tibafit.controller.sport",
	    "com.tibafit.controller.customsport",
	    "com.tibafit.controller.workoutplan",
	    "com.tibafit.controller.workoutplan",
	    "com.tibafit.controller.workoutplanrecord",
	    "com.tibafit.controller.sporttype",
	    "com.tibafit.controller.sporttypeitem"
	})
public class SportApiAdvice {

    // @Valid failed
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponseDTO<Void> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldError() != null
                ? ex.getBindingResult().getFieldError().getDefaultMessage()
                : "SApiAdvice Annotation Valid failed";
        return ApiResponseDTO.parameterError(msg);
    }

    // JSON format wrong or cannot parse
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponseDTO<Void> handleNotReadable(HttpMessageNotReadableException ex) {
        return ApiResponseDTO.parameterProgramError("SApiAdvice JSON format wrong or cannot parse. ErrMsg: " + ex.getMessage());
    }

    // illegal argument
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponseDTO<Void> handleIllegalArgument(IllegalArgumentException ex) {
        return ApiResponseDTO.parameterProgramError("SApiAdvice Illegal argument error. ErrMsg: " + ex.getMessage());
    }

    // general Exception
    @ExceptionHandler(Exception.class)
    public ApiResponseDTO<Void> handleException(Exception ex) {
        ex.printStackTrace();
        return ApiResponseDTO.systemError("SApiAdvice System error. ErrMsg: " + ex.getMessage());
    }
}
