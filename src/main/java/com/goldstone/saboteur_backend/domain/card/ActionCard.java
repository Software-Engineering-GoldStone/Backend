package com.goldstone.saboteur_backend.domain.card;

import com.goldstone.saboteur_backend.domain.board.Cell;
import com.goldstone.saboteur_backend.domain.enums.ActionCardType;
import com.goldstone.saboteur_backend.domain.enums.GoalCardType;
import com.goldstone.saboteur_backend.domain.enums.TargetToolType;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.CardErrorCode;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActionCard extends Card {
    private ActionCardType type;
    private TargetToolType tool;
    private Cell targetCell;
    private Set<TargetToolType> tools = new HashSet<>();

    @Override
    public void use() {
        switch (type) {
            case FALLING_ROCK -> targetCell.removeCard();
            case MAP -> peekDestinationCard(targetCell);
            default -> throw new BusinessException(CardErrorCode.INVALID_ACTION_CARD);
        }
    }

    @Override
    public boolean availableUse() {
        return false;
    }

    // repairTool
    public void repairTool(Set<TargetToolType> tools) {
        for (TargetToolType tool : tools) {
            if (this.tools.contains(tool)) {
                this.tools.remove(tool);
            } else {
                this.tools.add(tool);
            }
        }
    }

    // destroyTool
    public void destoryTool(TargetToolType tool) {
        // 유저 길놓기 비활성화?
    }

    // peekDestinationCard
    public GoalCardType peekDestinationCard(Cell targetCell) {

        if (!(targetCell.getCard() instanceof GoalCard)
                || targetCell == null
                || targetCell.isEmptyCard()) {
            throw new BusinessException(CardErrorCode.INVALID_GOAL_CARD);
        }

        GoalCard goalCard = (GoalCard) targetCell.getCard();
        return goalCard.getType();
    }
}
