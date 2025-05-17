package com.goldstone.saboteur_backend.domain.card;

import java.util.Set;

import lombok.Getter;

@Getter
public abstract class Card {

    protected final Set<Direction> paths;
    
    protected Card(Set<Direction> paths) {
        this.paths = paths;
    }
    
    public abstract boolean hasPath(Direction direction);
    
    public abstract boolean canConnectTo(Card otherCard, Direction direction);
}
