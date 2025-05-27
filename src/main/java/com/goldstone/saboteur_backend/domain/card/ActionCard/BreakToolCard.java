package com.goldstone.saboteur_backend.domain.card.ActionCard;

import com.goldstone.saboteur_backend.domain.card.UsableOnUser;
import com.goldstone.saboteur_backend.domain.enums.PlayerToolStatus;
import com.goldstone.saboteur_backend.domain.enums.TargetToolType;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.CardErrorCode;

public class BreakToolCard extends ActionCard implements UsableOnUser {
    private TargetToolType breakTool;

    public BreakToolCard(TargetToolType breakTool) {
        this.breakTool = breakTool;
    }

    @Override
    public void use(User targetUser) {
        if (targetUser == null || breakTool == null) {
            throw new BusinessException(CardErrorCode.INVALID_ACTION_CARD);
        }

        PlayerToolStatus playerToolStatus = targetUser.getToolStatusMap().get(breakTool);
        if (playerToolStatus == PlayerToolStatus.BROKEN) {
            throw new BusinessException(CardErrorCode.INVALID_ACTION_CARD);
        }

        targetUser.breakTool(breakTool);
    }

    @Override
    public boolean availableUse() {
        return breakTool != null;
    }
}
