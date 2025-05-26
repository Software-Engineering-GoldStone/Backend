package com.goldstone.saboteur_backend.socketIo.eventRegister;

import com.corundumstudio.socketio.SocketIOServer;

public interface SocketEventRegister {
    void registerEvents(SocketIOServer server);
}
