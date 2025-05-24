package com.goldstone.saboteur_backend.domain.board;

import com.goldstone.saboteur_backend.domain.card.GoalCard;
import com.goldstone.saboteur_backend.domain.card.PathCard;
import com.goldstone.saboteur_backend.domain.card.StartCard;
import com.goldstone.saboteur_backend.domain.enums.GoalCardType;
import com.goldstone.saboteur_backend.domain.enums.PathCardType;
import java.util.*;
import lombok.Getter;

@Getter
public class Board {
    public static final int DEFAULT_WIDTH = 9;
    public static final int DEFAULT_HEIGHT = 5;

    public static final Integer DEFAULT_GOAL_CELL_Y_LIST[] = {0, 2, 4};

    private final Set<Cell> dynamicCells = new HashSet<>();

    public Board() {
        for (int y = 0; y < DEFAULT_HEIGHT; y++) {
            for (int x = 0; x < DEFAULT_WIDTH; x++) {
                dynamicCells.add(new Cell(x, y));
            }
        }

        // 시작 카드 배치
        Cell startCell = this.getCellFromXAndY(0, DEFAULT_HEIGHT / 2);
        startCell.setCard(new StartCard());

        // 골 카드 배치
        List<GoalCard> goalCards =
                Arrays.asList(
                        new GoalCard(GoalCardType.GOLD, PathCardType.CROSSROAD),
                        new GoalCard(GoalCardType.EMPTY, PathCardType.LEFT_TURN),
                        new GoalCard(GoalCardType.EMPTY, PathCardType.RIGHT_TURN));
        Collections.shuffle(goalCards);

        for (int i = 0; i < DEFAULT_GOAL_CELL_Y_LIST.length; i++) {
            int x = DEFAULT_WIDTH - 1;
            int y = DEFAULT_GOAL_CELL_Y_LIST[i];
            GoalCard goalCard = goalCards.get(i);

            this.getCellFromXAndY(x, y).setCard(goalCard);
        }
    }

    public Cell getCellFromXAndY(int x, int y) {
        Cell targetcell = new Cell(x, y);
        if (this.dynamicCells.contains(targetcell)) {
            return this.dynamicCells.stream().filter(targetcell::equals).findFirst().get();
        }
        return null;
    }

    public boolean placeCard(int x, int y, PathCard card) {
        Cell targetCell = getOrCreateCell(x, y);

        if (targetCell.canPlacePathCard(card)) {
            targetCell.setCard(card);
            return true;
        }

        return false;
    }

    public Cell getOrCreateCell(int x, int y) {
        Cell cell = this.getCellFromXAndY(x, y);
        if (cell == null) {
            Cell newCell = new Cell(x, y);
            this.dynamicCells.add(newCell);

            return newCell;
        }
        return cell;
    }

    public Cell[] getGoals() {
        return new Cell[] {
            this.getCellFromXAndY(DEFAULT_WIDTH - 1, 0),
            this.getCellFromXAndY(DEFAULT_WIDTH - 1, 2),
            this.getCellFromXAndY(DEFAULT_WIDTH - 1, 4)
        };
    }

    // 두 카드 간 길이 이어지는지 확인하는 메서드
    public boolean isConnected(Cell fromCell, Cell toCell) {
        if (fromCell == null || toCell == null) return false;

        int dx = toCell.getX() - fromCell.getX();
        int dy = toCell.getY() - fromCell.getY();

        int direction = -1;
        if (dx == 0 && dy == -1) direction = 0; // 위
        else if (dx == 1 && dy == 0) direction = 1; // 오른쪽
        else if (dx == 0 && dy == 1) direction = 2; // 아래
        else if (dx == -1 && dy == 0) direction = 3; // 왼쪽

        if (direction == -1) return false; // 인접하지 않은 셀

        return PathValidator.isConnected(fromCell, toCell, direction);
    }
}
