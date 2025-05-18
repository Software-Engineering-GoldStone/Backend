package com.goldstone.saboteur_backend.domain.card;

import com.goldstone.saboteur_backend.domain.Cell;
import lombok.Getter;

@Getter
public class PathCard extends Card {
    public static Cell.Side Side;
    private Cell.Side[] sides;

    public PathCard(String id, String name, Cell.Side[] sides) {
        super(id, name, Card.CardType.PATHWAY); // CardType 수정
        this.sides = sides;
    }

    public PathCard rotate(int times) {
        times = times % 4;
        // 0도(0)와 180도(2)만 허용
        if (times != 0 && times != 2) {
            return this;
        }

        Cell.Side[] newSides = new Cell.Side[4];
        for (int i = 0; i < 4; i++) {
            newSides[(i + times) % 4] = sides[i];
        }
        sides = newSides;
        return this;
    }

    public PathCard copy() {
        return new PathCard(getId(), getName(), sides.clone());
    }
}
