package com.goldstone.saboteur_backend.domain.game;

import com.goldstone.saboteur_backend.domain.enums.GameRole;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class GameRoundManager {
    private LocalDateTime roundStartTime;
    private LocalDateTime roundEndTime;
    private GameRole winnerRole;

    public void startRound() {
        this.roundStartTime = LocalDateTime.now();
        this.winnerRole = null;
    }

    public void endRound(GameRole winnerRole) {
        this.roundEndTime = LocalDateTime.now();
        this.winnerRole = winnerRole;
    }
}
