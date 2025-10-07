/**
 * 
 */
package com.tibafit.dto.sport;

public class ApiResponseDTO<T> {
    private String returnCode;
    private String returnMsg;
    private T returnData;

    public ApiResponseDTO() {
    	super();
    }

    public ApiResponseDTO(String returnCode, String returnMsg, T returnData) {
        this.returnCode = returnCode;
        this.returnMsg = returnMsg;
        this.returnData = returnData;
    }

    public static <T> ApiResponseDTO<T> success(T returnData) {
        return new ApiResponseDTO<>("000", "success", returnData);
    }

    public static <T> ApiResponseDTO<T> parameterError(String msg) {
        return new ApiResponseDTO<>("P500", msg, null);
    }
    
    public static <T> ApiResponseDTO<T> parameterProgramError(String msg) {
        return new ApiResponseDTO<>("PG500", msg, null);
    }
    
    public static <T> ApiResponseDTO<T> repeatError(String msg) {
        return new ApiResponseDTO<>("R500", msg, null);
    }

    public static <T> ApiResponseDTO<T> systemError(String msg) {
        return new ApiResponseDTO<>("S500", msg, null);
    }
    
    public static <T> ApiResponseDTO<T> loginError(String msg) {
        return new ApiResponseDTO<>("L500", msg, null);
    }


    public String getReturnCode() { 
    	return returnCode; 
    }
    public void setReturnCode(String returnCode) { 
    	this.returnCode = returnCode; 
    }
    public String getReturnMsg() { 
    	return returnMsg; 
    }
    public void setReturnMsg(String returnMsg) { 
    	this.returnMsg = returnMsg; 
    }
    public T getReturnData() { 
    	return returnData; 
    }
    public void setReturnData(T returnData) { 
    	this.returnData = returnData; 
    }
}
