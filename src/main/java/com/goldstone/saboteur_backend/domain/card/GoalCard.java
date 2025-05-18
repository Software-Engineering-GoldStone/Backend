package com.goldstone.saboteur_backend.domain.card;

import com.goldstone.saboteur_backend.domain.enums.GoalCardType;
import lombok.Getter;

@Getter
public class GoalCard extends Card {
    private final GoalCardType goalType;

    public GoalCard(String id, String name, GoalCardType goalType) {
        super(id, name, CardType.GOAL);
        this.goalType = goalType;
    }

}
