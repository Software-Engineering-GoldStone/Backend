package com.goldstone.saboteur_backend.domain.card;

import lombok.Getter;

@Getter
public abstract class Card {
    private final String id;
    private final String name;
    private final CardType type;
    private final String imageUrl;

    public enum CardType {
        PATHWAY, ACTION, GOAL, ROLE  // ROLE 추가
    }

    public Card(String id, String name, CardType type) {
        this.id = id;
        this.name = name;
        this.type = type;
        // resources의 카드 이미지 사용
        this.imageUrl = "/img/cards/" + id + ".png";
    }

}
