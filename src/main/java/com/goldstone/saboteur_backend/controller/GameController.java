package com.goldstone.saboteur_backend.controller;

import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.service.game.GameService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/play-card")
    public boolean playCard(@RequestParam User user, @RequestParam Card card) {
        return gameService.playCard(user, card);
    }

    @PostMapping("/next-turn")
    public User nextTurn() {
        return gameService.nextTurn();
    }
}
