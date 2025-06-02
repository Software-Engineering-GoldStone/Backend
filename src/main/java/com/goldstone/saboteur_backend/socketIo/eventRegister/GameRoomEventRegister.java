package com.goldstone.saboteur_backend.socketIo.eventRegister;

import com.corundumstudio.socketio.SocketIOServer;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.dtos.gameRoom.request.JoinGameRoomRequestDto;
import com.goldstone.saboteur_backend.dtos.gameRoom.request.StartGameRequestDto;
import com.goldstone.saboteur_backend.dtos.gameRoom.response.JoinGameRoomResponseDto;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.ErrorCode;
import com.goldstone.saboteur_backend.exception.responseDto.ErrorResponse;
import com.goldstone.saboteur_backend.service.gameRoom.GameRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GameRoomEventRegister implements SocketEventRegister {
    private final GameRoomService gameRoomService;

    @Override
    public void registerEvents(SocketIOServer server) {
        server.addEventListener(
                "joinGameRoom",
                JoinGameRoomRequestDto.class,
                (client, data, ackSender) -> {
                    try {
                        GameRoom gameRoom = this.gameRoomService.joinGameRoom(client, data);

                        client.sendEvent("gameRoomJoined", JoinGameRoomResponseDto.from(gameRoom));
                    } catch (Exception e) {
                        if (e instanceof BusinessException) {
                            ErrorCode errorCode = ((BusinessException) e).getErrorCode();
                            client.sendEvent("errorEvent", new ErrorResponse(errorCode));
                        } else {
                            client.sendEvent("errorEvent", ErrorResponse.internalServerError());
                        }
                    }
                });
        server.addEventListener(
                "startGame",
                StartGameRequestDto.class,
                (client, data, ackSender) -> this.gameRoomService.startGame(data));
    }
}
