package com.goldstone.saboteur_backend.domain.card.actionCard;

import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.enums.ActionCardType;
import com.goldstone.saboteur_backend.domain.enums.CardType;
import com.goldstone.saboteur_backend.domain.enums.TargetToolType;
import com.goldstone.saboteur_backend.domain.user.User;
import lombok.Getter;

@Getter
public abstract class ActionCard extends Card {
    private ActionCardType actionCardType;

    public ActionCard(ActionCardType actionCardType) {
        this.actionCardType = actionCardType;
    }

    public ActionCardType getActionCardType() {
        return actionCardType;
    }

    public CardType getCardType() {
        return CardType.ACTION;
    }

    @Override
    public void use() {
        throw new UnsupportedOperationException("타겟이 필요한 카드입니다. usePathCard(target)를 호출하세요.");
    }

    public abstract boolean availableUse();
}
