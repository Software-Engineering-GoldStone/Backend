package com.goldstone.saboteur_backend.domain.board;

import com.goldstone.saboteur_backend.domain.card.PathCard;
import com.goldstone.saboteur_backend.domain.enums.PathType;
import org.springframework.stereotype.Component;

@Component
public class PathValidator {
    public static boolean canPlacePathCard(Board board, Cell cell, PathCard pathCard) {
        if (cell == null || pathCard == null) {
            return false;
        }

        if (!cell.canPlacePathCard()) {
            return false;
        }

        // 검증을 위한 card set
        cell.setCard(pathCard);

        // up, right,down, left
        int dx[] = {0, 1, 0, -1};
        int dy[] = {1, 0, -1, 0};

        for (int i = 0; i < 4; i++) {
            int nx = cell.getX() + dx[i];
            int ny = cell.getY() + dy[i];
            Cell toCell = board.getCellFromXAndY(nx, ny);

            if (toCell == null) {
                continue;
            }

            if (!PathValidator.isConnected(cell, toCell, i)) {
                cell.removeCard();
                return false;
            }
        }

        cell.removeCard();
        return true;
    }

    // from: 기준 셀, to: 인접 셀
    // direction: 0: 위, 1: 오른쪽, 2: 아래, 3: 왼쪽
    public static boolean isConnected(Cell from, Cell to, int direction) {
        if (!from.isEmptyCard() || !to.isEmptyCard()) return false;

        int opposite = (direction + 2) % 4;

        PathType fromSide = from.getSides()[direction];
        PathType toSide = to.getSides()[opposite];

        return fromSide == PathType.PATH && toSide == PathType.PATH;
    }
}
