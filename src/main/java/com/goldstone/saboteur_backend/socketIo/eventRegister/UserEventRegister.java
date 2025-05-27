package com.goldstone.saboteur_backend.socketIo.eventRegister;

import com.corundumstudio.socketio.SocketIOServer;
import com.goldstone.saboteur_backend.dtos.user.request.CreateUserRequestDto;
import com.goldstone.saboteur_backend.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventRegister implements SocketEventRegister {

    private final UserService userService;

    @Override
    public void registerEvents(SocketIOServer server) {
        server.addEventListener(
                "createUser",
                CreateUserRequestDto.class,
                (client, data, ackSender) -> this.userService.createUser(client, data));
    }
}
