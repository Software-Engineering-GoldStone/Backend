package com.goldstone.saboteur_backend.domain.card;

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
        targetUser.breakTool(breakTool);
    }
}
