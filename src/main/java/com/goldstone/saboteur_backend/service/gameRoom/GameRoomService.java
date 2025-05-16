package com.goldstone.saboteur_backend.service.gameRoom;

import com.corundumstudio.socketio.SocketIOClient;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.domain.user.User;

public interface GameRoomService {
    void joinUser(SocketIOClient client, GameRoom gameRoom, User user) throws Exception;
}
