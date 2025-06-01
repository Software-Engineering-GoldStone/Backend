package com.goldstone.saboteur_backend.service.user;

import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.dtos.user.request.CreateUserRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Override
    public User createUser(CreateUserRequestDto dto) throws Exception {
        return new User(dto.getNickname(), dto.getBirthDate());
    }
}
