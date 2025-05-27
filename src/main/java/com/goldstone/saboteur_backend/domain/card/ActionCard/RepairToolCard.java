package com.goldstone.saboteur_backend.domain.card.ActionCard;

import com.goldstone.saboteur_backend.domain.enums.PlayerToolStatus;
import com.goldstone.saboteur_backend.domain.enums.TargetToolType;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.CardErrorCode;
import java.util.HashSet;
import java.util.Set;

public class RepairToolCard extends ActionCard {
    private final Set<TargetToolType> repairableTools;
    private TargetToolType targetTool;

    public RepairToolCard(Set<TargetToolType> repairableTools) {
        this.repairableTools = new HashSet<>(repairableTools);
    }

    public void selectTool(TargetToolType repairTool) {
        if (!repairableTools.contains(repairTool)) {
            throw new BusinessException(CardErrorCode.INVALID_ACTION_CARD);
        }
        this.targetTool = repairTool;
    }

    @Override
    public void use(User targetUser) {
        if (targetUser == null || targetTool == null) {
            throw new BusinessException(CardErrorCode.INVALID_ACTION_CARD);
        }

        PlayerToolStatus playerToolStatus = targetUser.getToolStatusMap().get(targetTool);
        if (playerToolStatus == PlayerToolStatus.FIXED) {
            throw new BusinessException(CardErrorCode.INVALID_ACTION_CARD);
        }

        targetUser.repairTools(Set.of(targetTool));
    }

    @Override
    public boolean availableUse() {
        return targetTool != null;
    }
}
