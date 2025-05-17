package com.goldstone.saboteur_backend.domain.card;

import java.util.EnumSet;

public class StartCard extends Card {
    
    private static final StartCard INSTANCE = new StartCard();

    private StartCard() {
        super(EnumSet.of(Direction.RIGHT));
    }

    public static StartCard getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean hasPath(Direction direction) {
        return paths.contains(direction);
    }

    @Override
    public boolean canConnectTo(Card otherCard, Direction direction) {
        return direction == Direction.RIGHT && otherCard.hasPath(Direction.LEFT);
    }
} 