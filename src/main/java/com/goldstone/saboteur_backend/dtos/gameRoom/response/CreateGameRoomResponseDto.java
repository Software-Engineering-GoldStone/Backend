package com.goldstone.saboteur_backend.dtos.gameRoom.response;

import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.dtos.user.response.UserInfoResponseDto;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CreateGameRoomResponseDto {
    private String id;
    private UserInfoResponseDto host;

    public static CreateGameRoomResponseDto from(GameRoom gameRoom) {
        return CreateGameRoomResponseDto.builder()
                .id(gameRoom.getId().toString())
                .host(UserInfoResponseDto.from(gameRoom.getSetting().getHost()))
                .build();
    }
}
