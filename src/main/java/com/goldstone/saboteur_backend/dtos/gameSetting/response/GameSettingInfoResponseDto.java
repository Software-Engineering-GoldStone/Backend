package com.goldstone.saboteur_backend.dtos.gameSetting.response;

import com.goldstone.saboteur_backend.domain.game.GameSetting;
import com.goldstone.saboteur_backend.dtos.user.response.UserInfoResponseDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameSettingInfoResponseDto {
    private final UserInfoResponseDto host;
    private final String title;
    private final Integer maxPlayers;
    private final Integer minPlayers;

    public static GameSettingInfoResponseDto from(GameSetting gameSetting) {
        return GameSettingInfoResponseDto.builder()
                .host(UserInfoResponseDto.from(gameSetting.getHost()))
                .title(gameSetting.getTitle())
                .maxPlayers(gameSetting.getMaxPlayers())
                .minPlayers(gameSetting.getMinPlayers())
                .build();
    }
}
