package com.tibafit.exception;


public class ValidationException extends RuntimeException {
    //自己定義的 Java Exception (錯誤) 類別。它繼承自 RuntimeException
    private final String field;

    public ValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
