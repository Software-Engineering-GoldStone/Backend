package com.goldstone.saboteur_backend.socketIo;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.goldstone.saboteur_backend.session.GlobalSession;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SocketIoService {

    @Autowired private final SocketIOServer server;
    @Autowired private final GlobalSession globalSession;

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
                    "Room ID, event na  me, data, and sender client must not be null");
        }
        server.getRoomOperations(roomId.toString()).getClients().stream()
                .filter(client -> !client.getSessionId().equals(senderClient.getSessionId()))
                .forEach(client -> client.sendEvent(eventName, data));
    }

    public <T> void sendEventToUser(UUID userId, String eventName, T data) {
        if (userId == null || eventName == null || data == null) {
            throw new IllegalArgumentException("User ID, event name, and data must not be null");
        }
        UUID socketId = globalSession.getSocketIdByUserId(userId);
        if (socketId == null) {
            throw new IllegalArgumentException("No socket found for user ID: " + userId);
        }
        SocketIOClient client = server.getClient(socketId);
        if (client != null) {
            client.sendEvent(eventName, data);
        }
    }
}
