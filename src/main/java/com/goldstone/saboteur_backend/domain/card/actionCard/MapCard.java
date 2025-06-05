package com.goldstone.saboteur_backend.domain.card.actionCard;

import com.goldstone.saboteur_backend.domain.board.Cell;
import com.goldstone.saboteur_backend.domain.card.GoalCard;
import com.goldstone.saboteur_backend.domain.enums.ActionCardType;
import com.goldstone.saboteur_backend.domain.enums.GoalCardType;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.CardErrorCode;

public class MapCard extends ActionCard {
    private Cell targetCell;
    private ActionCardType actionCardType;

    public MapCard() {
        super(ActionCardType.MAP);
    }

    public GoalCardType peekDestinationCard(Cell cell) {
        targetCell = cell;

        if (targetCell == null || targetCell.isEmptyCard()) {
            throw new BusinessException(CardErrorCode.INVALID_GOAL_CARD);
        }

        GoalCard goalCard = (GoalCard) targetCell.getCard();
        if (!(goalCard instanceof GoalCard)){
            throw new BusinessException(CardErrorCode.INVALID_GOAL_CARD);
        }
        return goalCard.getType();
    }

    @Override
    public boolean availableUse() {
        return targetCell != null;
    }
}
