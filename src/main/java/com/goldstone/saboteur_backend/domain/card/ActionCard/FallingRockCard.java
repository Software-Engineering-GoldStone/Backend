package com.goldstone.saboteur_backend.domain.card.ActionCard;

import com.goldstone.saboteur_backend.domain.board.Cell;
import com.goldstone.saboteur_backend.domain.card.UsableOnCell;

public class FallingRockCard extends ActionCard implements UsableOnCell {
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
