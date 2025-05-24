package com.goldstone.saboteur_backend.exception.code.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CardErrorCode implements ErrorCode {
    NO_CARDS_LEFT(HttpStatus.CONFLICT, "C001", "남은 카드가 없습니다."),
    INVALID_ACTION_CARD(HttpStatus.BAD_REQUEST, "C002", "유효하지 않은 ActionCard입니다."),
    INVALID_GOAL_CARD(HttpStatus.BAD_REQUEST, "C003", "유효하지 않은 GoalCard입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
