package com.goldstone.saboteur_backend.service.gameRoom;

import com.corundumstudio.socketio.SocketIOClient;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.dtos.gameRoom.request.CreateGameRoomRequestDto;
import com.goldstone.saboteur_backend.dtos.gameRoom.request.JoinGameRoomRequestDto;
import com.goldstone.saboteur_backend.dtos.gameRoom.request.StartGameRequestDto;

public interface GameRoomService {
    GameRoom createGameRoom(CreateGameRoomRequestDto dto);

    GameRoom joinGameRoom(SocketIOClient client, JoinGameRoomRequestDto dto);

    void startGame(StartGameRequestDto dto) throws Exception;
}
