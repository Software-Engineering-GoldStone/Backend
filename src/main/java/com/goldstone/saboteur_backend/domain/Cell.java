package com.goldstone.saboteur_backend.domain;

import com.goldstone.saboteur_backend.domain.card.Card;
import lombok.Getter;

@Getter
public class Cell {
    public enum Side {
        PATH,
        DEADEND
    }
    private Card card; // 해당 칸에 놓인 카드

    public void placeCard(Card card) {
        this.card = card;
    }

    public void removeCard() {
        this.card = null;
    }

    public boolean hasCard() {
        return card != null;
    }

    public Cell.Side[] getSides() {
        if (card instanceof com.goldstone.saboteur_backend.domain.card.PathCard) {
            return ((com.goldstone.saboteur_backend.domain.card.PathCard) card).getSides();
        }
        // 목표카드 등은 필요시 별도 처리
        return new Cell.Side[]{Cell.Side.DEADEND, Cell.Side.DEADEND, Cell.Side.DEADEND, Cell.Side.DEADEND};
    }
}
