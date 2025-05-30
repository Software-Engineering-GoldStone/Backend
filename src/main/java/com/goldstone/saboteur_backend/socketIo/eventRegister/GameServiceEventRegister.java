package com.goldstone.saboteur_backend.socketIo.eventRegister;

import com.corundumstudio.socketio.SocketIOServer;
import com.goldstone.saboteur_backend.dtos.game.request.DiscardCardRequestDto;
import com.goldstone.saboteur_backend.dtos.game.request.GetGameStateRequestDto;
import com.goldstone.saboteur_backend.dtos.game.request.NextTurnRequestDto;
import com.goldstone.saboteur_backend.dtos.game.request.PlayCardRequestDto;
import com.goldstone.saboteur_backend.service.game.GameHandleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GameServiceEventRegister implements SocketEventRegister {
    private final GameHandleService gameHandleService;

    @Override
    public void registerEvents(SocketIOServer server) {
        server.addEventListener(
                "playCard",
                PlayCardRequestDto.class,
                (client, data, ackSender) -> this.gameHandleService.playCard(client, data));

        server.addEventListener(
                "nextTurn",
                NextTurnRequestDto.class,
                (client, data, ackSender) -> this.gameHandleService.nextTurn(client, data));

        server.addEventListener(
                "getGameState",
                GetGameStateRequestDto.class,
                (client, data, ackSender) -> this.gameHandleService.getGameState(client, data));

        server.addEventListener(
                "discardCard",
                DiscardCardRequestDto.class,
                (client, data, ackSender) -> this.gameHandleService.discardCard(client, data));
    }
}
