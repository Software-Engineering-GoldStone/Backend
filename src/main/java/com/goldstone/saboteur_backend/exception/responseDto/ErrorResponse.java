package com.goldstone.saboteur_backend.exception.responseDto;

import com.goldstone.saboteur_backend.exception.code.error.ErrorCode;
import java.util.List;
import lombok.Getter;

@Getter
public class ErrorResponse {

    private final String code;
    private final String message;
    private final List<String> errors;

    public ErrorResponse(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.errors = null;
    }

    public ErrorResponse(ErrorCode errorCode, List<String> errors) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.errors = errors;
    }

    public ErrorResponse(String code, String message, List<String> errors) {
        this.code = code;
        this.message = message;
        this.errors = errors;
    }
}
