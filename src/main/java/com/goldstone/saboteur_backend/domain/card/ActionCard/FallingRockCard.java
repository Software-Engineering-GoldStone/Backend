package com.goldstone.saboteur_backend.domain.card.ActionCard;

import com.goldstone.saboteur_backend.domain.board.Cell;

public class FallingRockCard extends ActionCard {
    private Cell targetCell;

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
