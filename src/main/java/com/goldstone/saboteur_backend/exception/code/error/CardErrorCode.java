package com.goldstone.saboteur_backend.exception.code.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CardErrorCode implements ErrorCode {
    NO_CARDS_LEFT(HttpStatus.CONFLICT, "C001", "남은 카드가 없습니다."),
    INVALID_ACTION_CARD(HttpStatus.BAD_REQUEST, "C002", "유효하지 않은 ActionCard입니다."),
    INVALID_GOAL_CARD(HttpStatus.BAD_REQUEST, "C003", "유효하지 않은 GoalCard입니다."),
    INVALID_PATH_CARD(HttpStatus.BAD_REQUEST, "C004", "유효하지 않은 PathCard입니다."),
    INVALID_CARD_TYPE(HttpStatus.BAD_REQUEST, "C005", "유효하지 않은 카드 타입입니다."),
    INVALID_CARD_ID(HttpStatus.BAD_REQUEST, "C006", "해당 카드를 찾을 수 없습니다."),
    INVALID_PLACE_CARD(HttpStatus.BAD_REQUEST, "C007", "해당 위치에 카드를 배치할 수 없습니다."),
    CANNOT_PLACE_CARD_BY_TOOL_BROKEN(HttpStatus.BAD_REQUEST, "C008", "파괴된 도구가 있어 길 카드를 사용할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
