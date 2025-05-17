package com.goldstone.saboteur_backend.domain.card;

import java.util.EnumSet;
import java.util.Set;

public class PathCard extends Card {
    
    public PathCard(Set<Direction> paths) {
        super(paths);
    }

    public void rotate() {
        // 카드 회전 (180도)
        Set<Direction> rotatedPaths = EnumSet.noneOf(Direction.class);
        for (Direction path : paths) {
            switch (path) {
                case UP: rotatedPaths.add(Direction.DOWN); break;
                case RIGHT: rotatedPaths.add(Direction.LEFT); break;
                case DOWN: rotatedPaths.add(Direction.UP); break;
                case LEFT: rotatedPaths.add(Direction.RIGHT); break;
            }
        }
        this.paths.clear();
        this.paths.addAll(rotatedPaths);
    }

    @Override
    public boolean hasPath(Direction direction) {
        return paths.contains(direction);
    }

    @Override
    public boolean canConnectTo(Card otherCard, Direction direction) {
        Direction oppositeDirection = direction.getOpposite();
        return otherCard.hasPath(oppositeDirection);
    }
} 