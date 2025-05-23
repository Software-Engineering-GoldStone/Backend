package com.goldstone.saboteur_backend.domain.board;

import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.card.PathCard;
import com.goldstone.saboteur_backend.domain.enums.PathType;
import java.util.Objects;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Cell {
    private int x;
    private int y;
    private Card card;
    private PathType[] sides;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.card = null;
        this.sides =
                new PathType[] {PathType.EMPTY, PathType.EMPTY, PathType.EMPTY, PathType.EMPTY};
    }

    public void setCard(Card card) {
        this.card = card;

        if (card instanceof PathCard) {
            this.sides = ((PathCard) card).getSides();
        }
    }

    public void removeCard() {
        this.card = null;
        this.sides =
                new PathType[] {PathType.EMPTY, PathType.EMPTY, PathType.EMPTY, PathType.EMPTY};
    }

    public boolean isEmptyCard() {
        return this.card == null;
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
        for (int i = 0; i < this.sides.length; i++) {
            if (!this.sides[i].equals(PathType.EMPTY)) {
                return false;
            }
        }
        return this.isEmptyCard();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;
        Cell cell = (Cell) o;
        return x == cell.x && y == cell.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}