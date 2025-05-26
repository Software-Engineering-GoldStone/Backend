package com.goldstone.saboteur_backend.domain.card;

import com.goldstone.saboteur_backend.domain.enums.TargetToolType;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.CardErrorCode;
import java.util.HashSet;
import java.util.Set;

public class RepairToolCard extends ActionCard implements UsableOnUser {
    private Set<TargetToolType> repairTools = new HashSet<>();

    public RepairToolCard(Set<TargetToolType> repairTools) {
        this.repairTools = repairTools;
    }

    @Override
    public void use(User targetUser) {
        if (targetUser == null || repairTools == null) {
            throw new BusinessException(CardErrorCode.INVALID_ACTION_CARD);
        }
        targetUser.repairTools(repairTools);
    }
}
