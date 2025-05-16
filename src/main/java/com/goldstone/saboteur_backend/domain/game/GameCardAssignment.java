package com.goldstone.saboteur_backend.domain.game;

import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.domain.card.Card;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GameCardAssignment {
    private List<User> users;

    private List<Card> shuffledCardQueue;

    // shuffleCards()

    // assignCards()
}
