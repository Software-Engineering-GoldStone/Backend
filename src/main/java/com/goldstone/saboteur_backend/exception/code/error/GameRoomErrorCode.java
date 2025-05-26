package com.goldstone.saboteur_backend.exception.code.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GameRoomErrorCode implements ErrorCode {
    GAME_ROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "G001", "게임 룸을 찾을 수 없습니다."),
    CANNOT_JOIN_MAX_PLAYER(HttpStatus.BAD_REQUEST, "G002", "최대 인원 수가 초과되어 게임 룸에 입장할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
