package com.goldstone.saboteur_backend.service.user;

import com.corundumstudio.socketio.SocketIOClient;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.dtos.user.request.CreateUserRequestDto;
import com.goldstone.saboteur_backend.dtos.user.response.UserInfoResponseDto;
import com.goldstone.saboteur_backend.session.GlobalSession;
import com.goldstone.saboteur_backend.socketIo.SocketIoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired private final GlobalSession globalSession;
    private final SocketIoService socketIoService;

    @Override
    public User createUser(SocketIOClient client, CreateUserRequestDto dto) throws Exception {
        User user = new User(dto.getUsername(), dto.getBirthDate());
        if (this.globalSession.getUserSession(user.getId()) != null) {
            throw new Exception("User already exists with ID: " + user.getId());
        }
        this.globalSession.addUserSession(user);

        client.sendEvent("userCreated", UserInfoResponseDto.from(user));

        return user;
    }
}
