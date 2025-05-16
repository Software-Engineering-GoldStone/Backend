package com.goldstone.saboteur_backend.service.gameRoom;

import com.corundumstudio.socketio.SocketIOClient;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.domain.user.User;
import org.springframework.stereotype.Service;

@Service
public class GameRoomServiceImpl implements GameRoomService {

    @Override
    public void joinUser(SocketIOClient client, GameRoom gameRoom, User user) throws Exception {
        try {
            if (gameRoom.getPlayers().size() >= gameRoom.getSetting().getMaxPlayers()) {
                throw new Exception("Max player reached");
            }
            gameRoom.getPlayers().add(user);
            client.joinRoom(gameRoom.getId().toString());
        } catch (Exception e) {
            client.sendEvent("error", e.getMessage());
        }
    }
}
