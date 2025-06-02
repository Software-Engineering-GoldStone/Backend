package com.goldstone.saboteur_backend.exception.code.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "E001", "잘못된 요청입니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "E002", "입력값이 올바르지 않습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E001", "서버 오류입니다."),
    FAILED_TO_MANIPULATE_GLOBAL_SESSION(
            HttpStatus.INTERNAL_SERVER_ERROR, "E002", "글로벌 세션 조작에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
