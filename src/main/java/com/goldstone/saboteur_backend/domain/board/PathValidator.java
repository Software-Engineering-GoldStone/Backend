package com.goldstone.saboteur_backend.domain.board;


import com.goldstone.saboteur_backend.domain.card.PathCard;
import com.goldstone.saboteur_backend.domain.enums.PathType;

public class PathValidator {

    public static boolean canPlacePathCard(Cell cell, PathCard pathCard){
        PathType[] cellSides = cell.getSides();
        PathType[] cardSides = pathCard.getPathCardType().getSides(pathCard.isRotated());


        if (cell == null || pathCard == null) {
            return false;
        }

        if (cell.getCard() != null) {
            return false;
        }

        for (int i = 0; i < 4; i++) {
            // null 값이 있는 경우 배치 불가
            if (cellSides[i] == null || cardSides[i] == null) {
                return false;
            }

            // 셀의 면과 카드의 면이 일치하지 않으면 배치 불가
            if (!cellSides[i].equals(PathType.EMPTY) && !cellSides[i].equals(cardSides[i])) {
                return false;
            }
        }

        return true;
    }

    //from: 기준 셀, to: 인접 셀
    //direction: 0: 위, 1: 오른쪽, 2: 아래, 3: 왼쪽
    public static boolean isConnected(Cell from, Cell to, int direction) {
        if (!from.hasCard() || !to.hasCard()) return false;

        int opposite = (direction + 2) % 4;

        PathType fromSide = from.getSides()[direction];
        PathType toSide = to.getSides()[opposite];

        return fromSide == PathType.PATH && toSide == PathType.PATH;
    }

}
