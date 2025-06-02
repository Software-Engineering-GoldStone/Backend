package com.goldstone.saboteur_backend.dtos.gameRoom.response;

import com.goldstone.saboteur_backend.domain.enums.GameRoomStatus;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.dtos.gameSetting.response.GameSettingInfoResponseDto;
import com.goldstone.saboteur_backend.dtos.user.response.UserInfoResponseDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameRoomInfoResponseDto {
    private final String id;
    private final GameRoomStatus status;
    private final Integer round;
    private final GameSettingInfoResponseDto setting;
    private final UserInfoResponseDto[] players;

    public static GameRoomInfoResponseDto from(GameRoom gameRoom) {
        return GameRoomInfoResponseDto.builder()
                .id(gameRoom.getId().toString())
                .status(gameRoom.getStatus())
                .round(gameRoom.getRound())
                .setting(GameSettingInfoResponseDto.from(gameRoom.getSetting()))
                .players(
                        gameRoom.getPlayers().stream()
                                .map(UserInfoResponseDto::from)
                                .toArray(UserInfoResponseDto[]::new))
                .build();
    }
}
