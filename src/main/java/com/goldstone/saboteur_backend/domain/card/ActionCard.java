package com.goldstone.saboteur_backend.domain.card;

import com.goldstone.saboteur_backend.domain.enums.ActionCardType;
import com.goldstone.saboteur_backend.domain.enums.TargetToolType;
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
    // peekDestinationCard

}
