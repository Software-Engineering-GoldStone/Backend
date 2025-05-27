package com.goldstone.saboteur_backend.socketIo;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SocketIoService {

    @Autowired private final SocketIOServer server;

    public <T> void sendBroadCast(UUID roomId, String eventName, T data) {
        if (roomId == null || eventName == null || data == null) {
            throw new IllegalArgumentException("Room ID, event name, and data must not be null");
        }
        server.getRoomOperations(roomId.toString()).sendEvent(eventName, data);
    }

    public <T> void sendBroadCastWithoutUser(
            UUID roomId, String eventName, T data, SocketIOClient senderClient) {
        if (roomId == null || eventName == null || data == null || senderClient == null) {
            throw new IllegalArgumentException(
                    "Room ID, event name, data, and sender client must not be null");
        }
        server.getRoomOperations(roomId.toString()).getClients().stream()
                .filter(client -> !client.getSessionId().equals(senderClient.getSessionId()))
                .forEach(client -> client.sendEvent(eventName, data));
    }
}
