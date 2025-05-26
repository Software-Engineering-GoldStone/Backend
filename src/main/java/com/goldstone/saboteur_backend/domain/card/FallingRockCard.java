package com.goldstone.saboteur_backend.domain.card;

import com.goldstone.saboteur_backend.domain.board.Cell;

public class FallingRockCard extends ActionCard implements UsableOnCell {

    @Override
    public void use(Cell targetCell) {
        targetCell.removeCard();
    }
}
