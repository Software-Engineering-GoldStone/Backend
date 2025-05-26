package com.goldstone.saboteur_backend.domain.card;

import com.goldstone.saboteur_backend.domain.board.Cell;

public interface UsableOnCell {
    void use(Cell targetCell);
}
