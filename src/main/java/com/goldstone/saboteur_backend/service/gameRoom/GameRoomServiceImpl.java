package com.goldstone.saboteur_backend.service.gameRoom;

import com.corundumstudio.socketio.SocketIOClient;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.dtos.gameRoom.request.CreateGameRoomRequestDto;
import com.goldstone.saboteur_backend.dtos.gameRoom.request.JoinGameRoomRequestDto;
import com.goldstone.saboteur_backend.dtos.gameRoom.response.CreateGameRoomResponseDto;
import com.goldstone.saboteur_backend.exception.code.error.GameRoomErrorCode;
import com.goldstone.saboteur_backend.exception.code.error.UserErrorCode;
import com.goldstone.saboteur_backend.session.GlobalSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameRoomServiceImpl implements GameRoomService {

    private final GlobalSession globalSession;

    @Override
    public GameRoom createGameRoom(SocketIOClient client, CreateGameRoomRequestDto dto)
            throws Exception {
        User host = this.globalSession.getUserSession(dto.getUserId());
        if (host == null) {
            throw new Exception(UserErrorCode.USER_NOT_FOUND.getMessage());
        }

        GameRoom gameRoom = GameRoom.createGameRoomByHost(host);
        this.globalSession.addGameRoomSession(gameRoom);

        CreateGameRoomResponseDto responseDto = CreateGameRoomResponseDto.from(gameRoom);

        client.joinRoom(gameRoom.getId().toString());
        client.sendEvent("gameRoomCreated", responseDto);

        return gameRoom;
    }

    @Override
    public void joinGameRoom(SocketIOClient client, JoinGameRoomRequestDto dto) throws Exception {
        try {
            User user = this.globalSession.getUserSession(dto.getUserId());
            if (user == null) {
                throw new Exception(UserErrorCode.USER_NOT_FOUND.getMessage());
            }

            GameRoom gameRoom = this.globalSession.getGameRoomSession(dto.getGameRoomId());
            if (gameRoom == null) {
                throw new Exception(GameRoomErrorCode.GAME_ROOM_NOT_FOUND.getMessage());
            }

            if (gameRoom.getUserGameRooms().size() + 1 > gameRoom.getSetting().getMaxPlayers()) {
                throw new Exception(GameRoomErrorCode.CANNOT_JOIN_MAX_PLAYER.getMessage());
            }

            gameRoom.addPlayer(user);

            client.joinRoom(gameRoom.getId().toString());
            client.sendEvent("gameRoomJoined");
        } catch (Exception e) {
            client.sendEvent("error", e.getMessage());
            throw e;
        }
    }
}
