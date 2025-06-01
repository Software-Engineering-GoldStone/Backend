package com.goldstone.saboteur_backend.exception;

import com.goldstone.saboteur_backend.exception.code.error.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // 메시지를 RuntimeException 에 전달
        this.errorCode = errorCode;
    }
}
