package com.goldstone.saboteur_backend.domain.game;

import com.goldstone.saboteur_backend.domain.enums.GameRole;
import com.goldstone.saboteur_backend.domain.mapping.UserGameRoom;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class GameRoundManager {
    private GameRoom gameRoom;
    private List<UserGameRoom> userGameRooms;
    private GameCardPool cardPool;
    private LocalDateTime roundStartTime;
    private LocalDateTime roundEndTime;
    private GameRole winnerRole;

    public GameRoundManager(
            GameRoom gameRoom, List<UserGameRoom> userGameRooms, GameCardPool cardPool) {
        this.gameRoom = gameRoom;
        this.userGameRooms = userGameRooms;
        this.cardPool = cardPool;
    }

    public void startRound() {
        this.roundStartTime = LocalDateTime.now();
        this.winnerRole = null;
    }

    public void endRound() {
        this.roundEndTime = LocalDateTime.now();
        this.winnerRole = GameRole.SABOTEUR; // 기본적으로 사보타지 승리  -> 해당부분은 금 목적지 카드가 오픈되었는지 여부에 따라 변경되어야 함
    }
}
