package com.goldstone.saboteur_backend.domain.card;

import com.goldstone.saboteur_backend.domain.enums.PathCardType;

public class StartCard extends PathCard {
    // 시작카드는 네 방향 모두 열린 길카드로 고정
    public StartCard() {
        super(PathCardType.CROSSROAD, false);
    }

    @Override
    public boolean availableUse() {
        return false;
    }

    @Override
    public void use() {}
}

