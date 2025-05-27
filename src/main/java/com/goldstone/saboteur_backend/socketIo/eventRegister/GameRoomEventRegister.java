package com.goldstone.saboteur_backend.socketIo.eventRegister;

import com.corundumstudio.socketio.SocketIOServer;
import com.goldstone.saboteur_backend.dtos.gameRoom.request.CreateGameRoomRequestDto;
import com.goldstone.saboteur_backend.dtos.gameRoom.request.JoinGameRoomRequestDto;
import com.goldstone.saboteur_backend.dtos.gameRoom.request.StartGameRequestDto;
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
                "createGameRoom",
                CreateGameRoomRequestDto.class,
                (client, data, ackSender) -> this.gameRoomService.createGameRoom(client, data));
        server.addEventListener(
                "joinGameRoom",
                JoinGameRoomRequestDto.class,
                (client, data, ackSender) -> this.gameRoomService.joinGameRoom(client, data));
        server.addEventListener(
                "startGame",
                StartGameRequestDto.class,
                (client, data, ackSender) -> this.gameRoomService.startGame(data));
    }
}
