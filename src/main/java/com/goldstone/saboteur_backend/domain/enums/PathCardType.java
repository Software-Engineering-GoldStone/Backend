package com.goldstone.saboteur_backend.domain.enums;

import lombok.Getter;

@Getter
public enum PathCardType {
    BOTH_HORIZONTAL_DEADEND(new PathType[]{PathType.ROCK, PathType.DEADEND, PathType.ROCK, PathType.DEADEND}),
    BOTH_VERTICAL_DEADEND(new PathType[]{PathType.DEADEND, PathType.ROCK, PathType.DEADEND, PathType.ROCK}),
    CROSSROAD_DEADEND(new PathType[]{PathType.DEADEND, PathType.DEADEND, PathType.DEADEND, PathType.DEADEND}),
    HORIZONTAL_T_DEADEND(new PathType[]{PathType.ROCK, PathType.DEADEND, PathType.DEADEND, PathType.DEADEND}),
    VERTICAL_T_DEADEND(new PathType[]{PathType.DEADEND, PathType.ROCK, PathType.DEADEND, PathType.DEADEND}),
    LEFT_TURN_DEADEND(new PathType[]{PathType.ROCK, PathType.ROCK, PathType.DEADEND, PathType.DEADEND}),
    RIGHT_TURN_DEADEND(new PathType[]{PathType.ROCK, PathType.DEADEND, PathType.DEADEND, PathType.ROCK}),
    SINGLE_HORIZONTAL_DEADEND(new PathType[]{PathType.ROCK, PathType.ROCK, PathType.ROCK, PathType.DEADEND}),
    SINGLE_VERTICAL_DEADEND(new PathType[]{PathType.ROCK, PathType.ROCK, PathType.DEADEND, PathType.ROCK}),

    CROSSROAD(new PathType[]{PathType.PATH, PathType.PATH, PathType.PATH, PathType.PATH}),
    HORIZONTAL(new PathType[]{PathType.ROCK, PathType.PATH, PathType.ROCK, PathType.PATH}),
    HORIZONTAL_T(new PathType[]{PathType.ROCK, PathType.PATH, PathType.PATH, PathType.PATH}),
    LEFT_TURN(new PathType[]{PathType.ROCK, PathType.ROCK, PathType.PATH, PathType.PATH}),
    RIGHT_TURN(new PathType[]{PathType.ROCK, PathType.PATH, PathType.PATH, PathType.ROCK}),
    VERTICAL(new PathType[]{PathType.PATH, PathType.ROCK, PathType.PATH, PathType.ROCK}),
    VERTICAL_T(new PathType[]{PathType.PATH, PathType.ROCK, PathType.PATH, PathType.PATH});

    private final PathType[] sides;

    PathCardType(PathType[] sides) {
        this.sides = sides;
    }

    public PathType[] getSides(boolean rotated) {
        if (!rotated) return sides;

        //카드 회전 시
        return new PathType[]{
                sides[2], // bottom → top
                sides[3], // left → right
                sides[0], // top → bottom
                sides[1]  // right → left
        };
    }
}
