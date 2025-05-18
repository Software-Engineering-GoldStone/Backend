package com.goldstone.saboteur_backend.domain.card;

import com.goldstone.saboteur_backend.domain.enums.ActionCardType;
import lombok.Getter;

@Getter
public class ActionCard extends Card {
    // @Override 어노테이션 제거
    private final ActionCardType actionType;

    public ActionCard(String id, String name, ActionCardType actionType) {
        super(id, name, Card.CardType.ACTION);  // Card.CardType으로 수정
        this.actionType = actionType;
    }

}
