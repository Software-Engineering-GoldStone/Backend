package com.goldstone.saboteur_backend.domain.card.actionCard;

import com.goldstone.saboteur_backend.domain.enums.ActionCardType;
import com.goldstone.saboteur_backend.domain.enums.PlayerToolStatus;
import com.goldstone.saboteur_backend.domain.enums.TargetToolType;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.CardErrorCode;
import lombok.Getter;

@Getter
public class BreakToolCard extends ActionCard {
    private TargetToolType targetTool;

    public BreakToolCard(TargetToolType breakTool) {
        super(ActionCardType.DESTROY);
        this.targetTool = breakTool;
    }

    @Override
    public void use(User targetUser) {
        if (targetUser == null || targetTool == null) {
            throw new BusinessException(CardErrorCode.INVALID_ACTION_CARD);
        }

        PlayerToolStatus playerToolStatus = targetUser.getToolStatusMap().get(targetTool);
        if (playerToolStatus == PlayerToolStatus.BROKEN) {
            throw new BusinessException(CardErrorCode.INVALID_ACTION_CARD);
        }

        targetUser.breakTool(targetTool);
    }

    @Override
    public boolean availableUse() {
        return targetTool != null;
    }
}
