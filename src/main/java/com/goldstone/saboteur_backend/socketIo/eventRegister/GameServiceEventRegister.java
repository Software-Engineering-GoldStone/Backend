package com.goldstone.saboteur_backend.socketIo.eventRegister;

import com.corundumstudio.socketio.SocketIOServer;
import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.dtos.game.request.*;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.ErrorCode;
import com.goldstone.saboteur_backend.exception.responseDto.ErrorResponse;
import com.goldstone.saboteur_backend.service.game.GameHandleService;
import java.util.List;
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
                (client, data, ackSender) -> {
                    try {
                        this.gameHandleService.playCard(client, data);
                    } catch (Exception e) {
                        System.err.println(data);
                        e.printStackTrace();
                    }
                });

        server.addEventListener(
                "nextTurn",
                NextTurnRequestDto.class,
                (client, data, ackSender) -> {
                    try {
                        this.gameHandleService.nextTurn(client, data);
                    } catch (Exception e) {
                        System.err.println(data);
                        e.printStackTrace();
                    }
                });

        server.addEventListener(
                "getGameState",
                GetGameStateRequestDto.class,
                (client, data, ackSender) -> {
                    try {
                        this.gameHandleService.getGameState(client, data);
                    } catch (Exception e) {
                        System.err.println(data);
                        e.printStackTrace();
                    }
                });

        server.addEventListener(
                "discardCard",
                DiscardCardRequestDto.class,
                (client, data, ackSender) -> {
                    try {
                        this.gameHandleService.discardCard(client, data);
                    } catch (Exception e) {
                        System.err.println(data);
                        e.printStackTrace();
                    }
                });

        server.addEventListener(
                "selectGoldCard",
                SelectGoldCardRequestDto.class,
                (client, data, ackSender) -> {
                    try {
                        this.gameHandleService.selectGoldCard(client, data);
                    } catch (Exception e) {
                        System.err.println(data);
                        e.printStackTrace();
                    }
                });

        server.addEventListener(
                "getUserDeck",
                GetUserDeckRequestDto.class,
                (client, data, ackSender) -> {
                    try {
                        List<Card> result =
                                this.gameHandleService.getUserDeck(
                                        data.getGameRoomId(), data.getUserId());
                        client.sendEvent("yourCardDeck", result);
                    } catch (Exception e) {
                        System.err.println(data);
                        e.printStackTrace();

                        if (e instanceof BusinessException) {
                            ErrorCode errorCode = ((BusinessException) e).getErrorCode();
                            client.sendEvent("errorEvent", new ErrorResponse(errorCode));
                        } else {
                            client.sendEvent("errorEvent", ErrorResponse.internalServerError());
                        }
                    }
                });
    }
}
