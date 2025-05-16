package com.goldstone.saboteur_backend.socketIo;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.dtos.gameRoom.request.CreateGameRoomRequestDto;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SocketIoController {
    private final SocketIOServer server;

    /** 소켓 이벤트 리스너 등록 */
    public SocketIoController(SocketIOServer server) {
        this.server = server;

        // 소켓 이벤트 리스너 등록
        server.addConnectListener(listenConnected());
        server.addDisconnectListener(listenDisconnected());

        server.addEventListener(
                "createGameRoom",
                CreateGameRoomRequestDto.class,
                (client, data, ackSender) -> GameRoom.createGameRoom(client, data));
    }

    public ConnectListener listenConnected() {
        return (client) -> {
            Map<String, List<String>> params = client.getHandshakeData().getUrlParams();
            log.info("connect:" + params.toString());
        };
    }

    public DisconnectListener listenDisconnected() {
        return client -> {
            String sessionId = client.getSessionId().toString();
            log.info("disconnect: " + sessionId);
            client.disconnect();
        };
    }
}
