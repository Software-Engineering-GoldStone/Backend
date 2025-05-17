package com.goldstone.saboteur_backend.domain.card;

public enum Direction {
    UP,
    RIGHT,
    DOWN,
    LEFT;

    public Direction getOpposite() {
        switch (this) {
            case UP: return DOWN;
            case RIGHT: return LEFT;
            case DOWN: return UP;
            case LEFT: return RIGHT;
            default: throw new IllegalStateException("Unknown direction: " + this);
        }
    }
} 