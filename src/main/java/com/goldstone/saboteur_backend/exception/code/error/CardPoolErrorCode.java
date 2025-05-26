package com.goldstone.saboteur_backend.exception.code.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CardPoolErrorCode implements ErrorCode {
    NO_CARDS_EXIST(HttpStatus.CONFLICT, "CP001", "카드 풀이 비어 있습니다."),
    SHUFFLE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "CP002", "카드 셔플 중 오류가 발생했습니다."),
    CARDPOOL_NOT_FOUND(HttpStatus.NOT_FOUND, "CP003", "카드 풀이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
