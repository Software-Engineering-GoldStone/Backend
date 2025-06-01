package com.goldstone.saboteur_backend.service.user;

import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.dtos.user.request.CreateUserRequestDto;

public interface UserService {
    User createUser(CreateUserRequestDto dto) throws Exception;
}
