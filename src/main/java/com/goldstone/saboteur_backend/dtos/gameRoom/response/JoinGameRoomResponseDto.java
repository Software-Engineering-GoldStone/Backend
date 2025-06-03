package com.goldstone.saboteur_backend.dtos.gameRoom.response;

import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.dtos.user.response.UserInfoResponseDto;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class JoinGameRoomResponseDto {
    private String gameRoomId;
    private int currentPlayerCount;
    private UserInfoResponseDto[] players;

    public static JoinGameRoomResponseDto from(GameRoom gameRoom) {
        return JoinGameRoomResponseDto.builder()
                .gameRoomId(gameRoom.getId().toString())
                .currentPlayerCount(gameRoom.getPlayers().size())
                .players(
                        gameRoom.getPlayers().stream()
                                .map(UserInfoResponseDto::from)
                                .toArray(UserInfoResponseDto[]::new))
                .build();
    }
}
