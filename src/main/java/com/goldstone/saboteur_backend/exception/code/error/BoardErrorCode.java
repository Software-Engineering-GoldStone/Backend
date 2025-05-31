package com.goldstone.saboteur_backend.exception.code.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BoardErrorCode implements ErrorCode {
    INVALID_PATH_PLACEMENT(HttpStatus.BAD_REQUEST, "B001", "해당 셀에 이미 카드가 존재합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
