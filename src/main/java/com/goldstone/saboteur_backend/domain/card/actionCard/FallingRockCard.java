package com.goldstone.saboteur_backend.domain.card.actionCard;

import com.goldstone.saboteur_backend.domain.board.Cell;
import com.goldstone.saboteur_backend.domain.enums.ActionCardType;

public class FallingRockCard extends ActionCard {
    private Cell targetCell;

    public FallingRockCard() {
        super(ActionCardType.FALLING_ROCK);
    }

    @Override
    public void use(Cell cell) {
        targetCell = cell;
        targetCell.removeCard();
    }

    @Override
    public boolean availableUse() {
        return targetCell != null;
    }
}
