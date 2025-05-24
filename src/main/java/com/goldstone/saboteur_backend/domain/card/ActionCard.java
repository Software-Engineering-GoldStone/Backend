package com.goldstone.saboteur_backend.domain.card;

import com.goldstone.saboteur_backend.domain.board.Cell;
import com.goldstone.saboteur_backend.domain.enums.ActionCardType;
import com.goldstone.saboteur_backend.domain.enums.GoalCardType;
import com.goldstone.saboteur_backend.domain.enums.TargetToolType;
import com.goldstone.saboteur_backend.domain.user.User;
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
    private TargetToolType tool; // 고장 카드용
    private Cell targetCell;
    private Set<TargetToolType> tools = new HashSet<>(); // 수리 카드용
    private User targetUser;

    @Override
    public void use() {
        switch (type) {
            case FALLING_ROCK -> targetCell.removeCard();
            case MAP -> peekDestinationCard(targetCell);
            case REPAIR -> repairTool();
            case DESTROY -> destroyTool();
            default -> throw new BusinessException(CardErrorCode.INVALID_ACTION_CARD);
        }
    }

    @Override
    public boolean availableUse() {
        return true;
    }

    // repairTool
    public void repairTool() {
        if (targetUser == null || tools == null) {
            throw new BusinessException(CardErrorCode.INVALID_ACTION_CARD);
        }
        targetUser.repairTools(tools);
    }

    // destroyTool
    public void destroyTool() {
        if (targetUser == null || tool == null) {
            throw new BusinessException(CardErrorCode.INVALID_ACTION_CARD);
        }
        targetUser.breakTool(tool);
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
