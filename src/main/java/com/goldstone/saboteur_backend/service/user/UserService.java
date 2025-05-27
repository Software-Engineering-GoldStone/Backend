package com.goldstone.saboteur_backend.service.user;

import com.corundumstudio.socketio.SocketIOClient;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.dtos.user.request.CreateUserRequestDto;

public interface UserService {
    User createUser(SocketIOClient client, CreateUserRequestDto dto) throws Exception;
}
