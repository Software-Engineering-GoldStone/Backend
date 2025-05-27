package com.goldstone.saboteur_backend.domain.card.ActionCard;

import com.goldstone.saboteur_backend.domain.board.Cell;
import com.goldstone.saboteur_backend.domain.card.GoalCard;
import com.goldstone.saboteur_backend.domain.enums.GoalCardType;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.CardErrorCode;

public class MapCard extends ActionCard {
    private Cell targetCell;

    public GoalCardType peekDestinationCard(Cell cell) {
        targetCell = cell;

        if (targetCell == null
                || !(targetCell.getCard() instanceof GoalCard)
                || targetCell.isEmptyCard()) {
            throw new BusinessException(CardErrorCode.INVALID_GOAL_CARD);
        }

        GoalCard goalCard = (GoalCard) targetCell.getCard();
        return goalCard.getType();
    }

    @Override
    public boolean availableUse() {
        return targetCell != null;
    }
}
