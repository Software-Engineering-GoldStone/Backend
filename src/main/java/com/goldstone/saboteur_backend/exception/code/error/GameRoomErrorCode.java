package com.goldstone.saboteur_backend.exception.code.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GameRoomErrorCode implements ErrorCode {
    GAME_ROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "G001", "게임 룸을 찾을 수 없습니다."),
    CANNOT_JOIN_MAX_PLAYER(HttpStatus.BAD_REQUEST, "G002", "최대 인원 수가 초과되어 게임 룸에 입장할 수 없습니다."),
    CANNOT_START_GAME(HttpStatus.BAD_REQUEST, "G003", "게임을 시작할 수 없습니다."),
    ALREADY_JOINED_GAME_ROOM(HttpStatus.BAD_REQUEST, "G004", "이미 게임 룸에 입장한 플레이어입니다."),
    CANNOT_START_GAME_NOT_HOST(HttpStatus.UNAUTHORIZED, "G005", "호스트만 게임을 시작할 수 있습니다."),
    CANNOT_JOIN_PLAYING_GAME_ROOM(HttpStatus.BAD_REQUEST, "G006", "게임이 진행 중인 게임 룸에는 입장할 수 없습니다."),
    GAME_BOARD_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "G007", "게임 보드를 찾을 수 없습니다.");
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
