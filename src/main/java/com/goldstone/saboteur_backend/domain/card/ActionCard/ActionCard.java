package com.goldstone.saboteur_backend.domain.card.ActionCard;

import com.goldstone.saboteur_backend.domain.card.Card;

public abstract class ActionCard extends Card {
    @Override
    public void use() {
        throw new UnsupportedOperationException("타겟이 필요한 카드입니다. use(target)를 호출하세요.");
    }

    public abstract boolean availableUse();
}
