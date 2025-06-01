package com.goldstone.saboteur_backend.domain.controller;

import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.dtos.gameRoom.request.CreateGameRoomRequestDto;
import com.goldstone.saboteur_backend.dtos.gameRoom.response.CreateGameRoomResponseDto;
import com.goldstone.saboteur_backend.service.gameRoom.GameRoomService;
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
@RequestMapping("/game-rooms")
@RequiredArgsConstructor
@Validated
public class GameRoomController {
    @Autowired private final GlobalSession globalSession;
    private final GameRoomService gameRoomService;

    @PostMapping
    public CreateGameRoomResponseDto createGameRoom(
            @RequestBody @Valid CreateGameRoomRequestDto dto) throws Exception {
        GameRoom gameRoom = this.gameRoomService.createGameRoom(dto);
        this.globalSession.addGameRoomSession(gameRoom);

        return CreateGameRoomResponseDto.from(gameRoom);
    }
}
