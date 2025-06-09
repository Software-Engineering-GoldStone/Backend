package com.goldstone.saboteur_backend.dtos.game.request;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class GetUserDeckRequestDto {
    private UUID gameRoomId;
    private UUID userId;
}
