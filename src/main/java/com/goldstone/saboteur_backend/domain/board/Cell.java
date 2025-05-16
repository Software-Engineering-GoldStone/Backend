package com.goldstone.saboteur_backend.domain.board;

import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.card.PathCard;
import com.goldstone.saboteur_backend.domain.enums.PathType;
import lombok.Getter;

@Getter
public class Cell {
    private int x;
    private int y;
    private Position position;
    private Card card;
    private PathType[] sides;


    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.position = new Position(x, y);
        this.card = null;
        this.sides = new PathType[]{PathType.EMPTY, PathType.EMPTY, PathType.EMPTY, PathType.EMPTY};
    }

    public void setCard(Card card) {
        this.card = card;

        if (card instanceof PathCard) {
            this.sides = ((PathCard) card).getSides();
        }
    }

    public void removeCard() {
        this.card = null;
        this.sides = new PathType[]{PathType.EMPTY, PathType.EMPTY, PathType.EMPTY, PathType.EMPTY};
    }

    public boolean hasCard() {
        return this.card != null;
    }

    public PathType topSide() {
        return sides[0];
    }

    public PathType rightSide() {
        return sides[1];
    }

    public PathType bottomSide() {
        return sides[2];
    }

    public PathType leftSide() {
        return sides[3];
    }

    public boolean canPlacePathCard(PathCard pathCard) {
        return PathValidator.canPlacePathCard(this, pathCard);
    }
}
