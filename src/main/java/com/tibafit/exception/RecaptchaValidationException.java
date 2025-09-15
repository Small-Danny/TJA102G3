package com.tibafit.exception;

import org.springframework.security.core.AuthenticationException;

public class RecaptchaValidationException extends AuthenticationException {

    public RecaptchaValidationException(String msg) {
        super(msg);
    }
}