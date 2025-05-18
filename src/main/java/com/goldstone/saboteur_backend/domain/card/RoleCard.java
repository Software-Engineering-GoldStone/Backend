package com.goldstone.saboteur_backend.domain.card;

import com.goldstone.saboteur_backend.domain.enums.GameRole;
import lombok.Getter;

@Getter
public class RoleCard extends Card {

    private final GameRole role;

    public RoleCard(String id, String name, GameRole role) {
        super(id, name, Card.CardType.ROLE);
        this.role = role;
    }

}
