package com.goldstone.saboteur_backend.dtos.game.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NextTurnResponseDto {
    private UUID nextPlayerId;
    private String nextPlayerName;
    private boolean gameEnded;
}
