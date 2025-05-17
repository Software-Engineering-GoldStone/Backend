package com.goldstone.saboteur_backend.domain.board;

import com.goldstone.saboteur_backend.domain.card.Card;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardPosition {
    private int x;
    private int y;
    private Card card;
    private boolean revealed;

    public BoardPosition(int x, int y) {
        this.x = x;
        this.y = y;
        this.revealed = false;
    }

    public void placeCard(Card card) {
        this.card = card;
    }

    public void reveal() {
        this.revealed = true;
    }

    public boolean isEmpty() {
        return card == null;
    }

    public boolean isRevealed() {
        return revealed;
    }

    @Override
    public String toString() {
        return String.format("Position(%d,%d)", x, y);
    }
} 