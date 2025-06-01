package com.goldstone.saboteur_backend.controller;

import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.dtos.user.request.CreateUserRequestDto;
import com.goldstone.saboteur_backend.dtos.user.response.UserInfoResponseDto;
import com.goldstone.saboteur_backend.service.user.UserService;
import com.goldstone.saboteur_backend.session.GlobalSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    @Autowired private final GlobalSession globalSession;
    private final UserService userService;

    @PostMapping
    public UserInfoResponseDto createUser(@RequestBody @Valid CreateUserRequestDto dto)
            throws Exception {
        User user = this.userService.createUser(dto);
        if (this.globalSession.getUserSession(user.getId()) != null) {
            throw new Exception("User already exists with ID: " + user.getId());
        }

        this.globalSession.addUserSession(user);

        return UserInfoResponseDto.from(user);
    }
}
