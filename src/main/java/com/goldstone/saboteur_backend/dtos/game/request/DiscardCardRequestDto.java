package com.goldstone.saboteur_backend.dtos.game.request;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiscardCardRequestDto {
    private UUID userId;
    private UUID gameRoomId;
    private UUID cardId;
}
