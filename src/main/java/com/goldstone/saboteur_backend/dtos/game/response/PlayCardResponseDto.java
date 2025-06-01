package com.goldstone.saboteur_backend.dtos.game.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayCardResponseDto {
    private boolean success;
    private String message;
    private int remainingCards;
}
