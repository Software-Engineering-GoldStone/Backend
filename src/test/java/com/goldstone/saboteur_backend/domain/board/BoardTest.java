package com.goldstone.saboteur_backend.domain.board;

import static org.junit.jupiter.api.Assertions.*;

import com.goldstone.saboteur_backend.domain.card.GoalCard;
import com.goldstone.saboteur_backend.domain.card.PathCard;
import com.goldstone.saboteur_backend.domain.card.StartCard;
import com.goldstone.saboteur_backend.domain.enums.PathCardType;
import com.goldstone.saboteur_backend.domain.enums.PathType;
import java.util.Arrays;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Board Test")
class BoardTest {

    @Test
    @DisplayName("보드 객체 생성시, 의도된 대로 초기화된다.")
    void initializeBoard() {
        Board board = new Board();

        assertEquals(45, board.getDynamicCells().size());

        Set<Cell> dynamicCells = board.getDynamicCells();
        Cell targetCell;
        Cell findedCell;

        // start card
        targetCell = new Cell(0, Board.DEFAULT_HEIGHT / 2);
        findedCell = dynamicCells.stream().filter(targetCell::equals).findFirst().get();
        assertNotNull(findedCell);
        assertFalse(findedCell.isEmptyCard());

        // goal card
        targetCell = new Cell(Board.DEFAULT_WIDTH - 1, 0);
        findedCell = dynamicCells.stream().filter(targetCell::equals).findFirst().get();
        assertNotNull(findedCell);
        assertFalse(findedCell.isEmptyCard());

        targetCell = new Cell(Board.DEFAULT_WIDTH - 1, 2);
        findedCell = dynamicCells.stream().filter(targetCell::equals).findFirst().get();
        assertNotNull(findedCell);
        assertFalse(findedCell.isEmptyCard());

        targetCell = new Cell(Board.DEFAULT_WIDTH - 1, 4);
        findedCell = dynamicCells.stream().filter(targetCell::equals).findFirst().get();
        assertNotNull(findedCell);
        assertFalse(findedCell.isEmptyCard());
    }

    @Test
    @DisplayName("x, y 좌표로부터 Cell에 대한 정보를 얻을 수 있다.")
    void getCellFromXAndY() {
        Board board = new Board();
        Cell startCell = board.getCellFromXAndY(0, 2);

        assertNotNull(startCell);
        assertEquals(0, startCell.getX());
        assertEquals(2, startCell.getY());
        assertFalse(startCell.isEmptyCard());
        assertInstanceOf(StartCard.class, startCell.getCard());

        PathType[] sides = startCell.getSides();
        for (int i = 0; i < 4; i++) {
            assertEquals(PathType.PATH, sides[i]);
        }
    }

    @Test
    @DisplayName("원하는 좌표에 카드를 놓을 수 있다.")
    void placeCard() {
        Board board = new Board();
        int x = 1;
        int y = 3;

        boolean result = board.placeCard(x, y, new PathCard(PathCardType.CROSSROAD, false));
        assertTrue(result);

        Cell newCell = board.getCellFromXAndY(x, y);

        assertNotNull(newCell);
        assertEquals(x, newCell.getX());
        assertEquals(y, newCell.getY());
        assertFalse(newCell.isEmptyCard());
        assertInstanceOf(PathCard.class, newCell.getCard());

        PathType[] sides = newCell.getSides();
        for (int i = 0; i < 4; i++) {
            assertEquals(PathType.PATH, sides[i]);
        }
    }

    @Test
    @DisplayName("이미 존재하는 Cell을 조회할 수 있다. 없는 Cell을 조회하려는 경우, 보드에 새로운 Cell이 추가되고, 보드의 크기가 커진다.")
    void getOrCreateCell() {
        Board board = new Board();

        Cell startCell = board.getOrCreateCell(0, 2);
        assertNotNull(startCell);
        assertEquals(45, board.getDynamicCells().size());

        // 시작 셀 왼족
        int x = -1;
        int y = 2;
        Cell newCell = board.getOrCreateCell(x, y);
        assertNotNull(newCell);
        assertEquals(46, board.getDynamicCells().size());
    }

    @Test
    @DisplayName("GoalCell 리스트와 각 GoalCell에 대한 정보를 얻을 수 있다.")
    void getGoals() {
        Board board = new Board();

        Cell goalCells[] = board.getGoals();

        assertNotNull(goalCells);
        assertEquals(3, goalCells.length);

        for (int i = 0; i < goalCells.length; i++) {
            assertInstanceOf(GoalCard.class, goalCells[i].getCard());
            assertEquals(Board.DEFAULT_WIDTH - 1, goalCells[i].getX());
            assertTrue(Arrays.asList(Board.DEFAULT_GOAL_CELL_Y_LIST).contains(goalCells[i].getY()));
        }
    }

    @Test
    @DisplayName("startCell부터 가로로 이어지는 pathCard를 셋팅했을 때, 모든 길은 이어져있다.")
    void isConnectedHorizontalPathFromStartCell() {
        int START_CELL_POS_Y = Board.DEFAULT_HEIGHT / 2;
        Board board = new Board();

        PathCard pathCard = new PathCard(PathCardType.CROSSROAD, false);
        int pos[][] = {
            {1, START_CELL_POS_Y},
            {2, START_CELL_POS_Y},
            {3, START_CELL_POS_Y},
            {4, START_CELL_POS_Y},
            {5, START_CELL_POS_Y},
            {6, START_CELL_POS_Y},
            {7, START_CELL_POS_Y},
        };

        for (int i = 0; i < pos.length; i++) {
            int x = pos[i][0], y = pos[i][1];
            board.placeCard(x, y, pathCard);
        }

        for (int i = 0; i < pos.length; i++) {
            int x = pos[i][0], y = pos[i][1];

            Cell fromCell = board.getCellFromXAndY(x - 1, y);
            Cell toCell = board.getCellFromXAndY(x, y);

            assertTrue(board.isConnected(fromCell, toCell));
        }
    }

    @Test
    @DisplayName("startCell부터 세로로 이어지는 pathCard를 셋팅했을 때, 모든 길은 이어져있고, 맵이 확장된다.")
    void isConnectedVerticalPathFromStartCellAndExpandedBoard() {
        Board board = new Board();

        assertEquals(45, board.getDynamicCells().size());

        PathCard pathCard = new PathCard(PathCardType.CROSSROAD, false);
        int pos[][] = {
            {0, 3}, {0, 4}, // 여기까지가 DEFAULT_HEIGHT에 의해 생성된 높이
            {0, 5}, {0, 6}, {0, 7}, {0, 8}, {0, 9}, {0, 10},
        };

        for (int i = 0; i < pos.length; i++) {
            int x = pos[i][0], y = pos[i][1];
            board.placeCard(x, y, pathCard);
        }

        assertNotEquals(45, board.getDynamicCells().size());
        assertEquals(45 + 6, board.getDynamicCells().size()); // 6개의 Cell이 추가됨

        for (int i = 0; i < pos.length; i++) {
            int x = pos[i][0], y = pos[i][1];

            Cell fromCell = board.getCellFromXAndY(x, y - 1);
            Cell toCell = board.getCellFromXAndY(x, y);

            assertTrue(board.isConnected(fromCell, toCell));
        }
    }
}
