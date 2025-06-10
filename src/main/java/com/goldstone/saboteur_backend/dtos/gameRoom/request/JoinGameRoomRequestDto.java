package com.goldstone.saboteur_backend.dtos.gameRoom.request;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class JoinGameRoomRequestDto {
    private UUID userId;
    private UUID gameRoomId;
}
