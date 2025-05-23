package com.goldstone.saboteur_backend.domain.card;

import com.goldstone.saboteur_backend.domain.board.Cell;
import com.goldstone.saboteur_backend.domain.enums.ActionCardType;
import com.goldstone.saboteur_backend.domain.enums.GoalCardType;
import com.goldstone.saboteur_backend.domain.enums.TargetToolType;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.CardErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ActionCard extends Card {
    private ActionCardType type;
    private TargetToolType tool;

    @Override
    public void use() {}

    @Override
    public boolean availableUse() {
        return false;
    }

    // repairTool
    // destroyTool

    // fallingRock
    public void fallingRock(Cell targetCell) {
        if (this.type != ActionCardType.FALLING_ROCK) {
            throw new BusinessException(CardErrorCode.INVALID_ACTION_CARD);
        }

        targetCell.removeCard();
    }

    // peekDestinationCard
    public GoalCardType peekDestinationCard(Cell targetCell) {
        if (this.type != ActionCardType.MAP) {
            throw new BusinessException(CardErrorCode.INVALID_ACTION_CARD);
        }

        if (!(targetCell.getCard() instanceof GoalCard)
                || targetCell == null
                || targetCell.isEmptyCard()) {
            throw new BusinessException(CardErrorCode.INVALID_GOAL_CARD);
        }

        GoalCard goalCard = (GoalCard) targetCell.getCard();
        return goalCard.getType();
    }
}
