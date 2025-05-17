package com.goldstone.saboteur_backend.domain.board;

import com.goldstone.saboteur_backend.domain.card.GoalCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void testBoardInitialization() {
        assertNotNull(board.getStartPosition());
        assertTrue(board.getStartPosition().isEmpty()); // 초기화 직후에는 카드가 없어야 함
        assertEquals(0, board.getStartPosition().getX()); // 시작 위치의 x 좌표가 0인지 확인
        assertEquals(2, board.getStartPosition().getY()); // 시작 위치의 y 좌표가 중앙인지 확인 (5/2 = 2)
        assertFalse(board.isInitialized());
    }

    @Test
    void testInitializeGoalCards() {
        List<GoalCard> goalCards = Arrays.asList(
            new GoalCard(),
            new GoalCard(),
            new GoalCard()
        );

        board.initializeGoalCards(goalCards);
        
        assertTrue(board.isInitialized());
        assertFalse(board.getStartPosition().isEmpty()); // 시작 카드가 배치되어 있어야 함
        assertTrue(board.getStartPosition().isRevealed()); // 시작 카드는 공개되어 있어야 함
        
        assertEquals(3, board.getGoalPositions().size());
        
        // 목표 카드들의 위치 확인
        assertTrue(board.isGoalPosition(board.getPosition(8, 0))); // 첫 번째 줄
        assertTrue(board.isGoalPosition(board.getPosition(8, 2))); // 중간 줄
        assertTrue(board.isGoalPosition(board.getPosition(8, 4))); // 마지막 줄
        
        for (BoardPosition goalPosition : board.getGoalPositions()) {
            assertFalse(goalPosition.isEmpty()); // 목표 카드가 배치되어 있어야 함
            assertFalse(goalPosition.isRevealed()); // 목표 카드는 비공개여야 함
            assertEquals(8, goalPosition.getX()); // x 좌표는 항상 8
        }
    }

    @Test
    void testInitializeGoalCardsThrowsExceptionWhenAlreadyInitialized() {
        List<GoalCard> goalCards = Arrays.asList(new GoalCard(), new GoalCard(), new GoalCard());
        board.initializeGoalCards(goalCards);
        
        assertThrows(IllegalStateException.class, () -> {
            board.initializeGoalCards(goalCards);
        });
    }

    @Test
    void testInitializeGoalCardsThrowsExceptionWhenNotThreeCards() {
        List<GoalCard> twoCards = Arrays.asList(new GoalCard(), new GoalCard());
        assertThrows(IllegalArgumentException.class, () -> {
            board.initializeGoalCards(twoCards);
        });

        List<GoalCard> fourCards = Arrays.asList(
            new GoalCard(), new GoalCard(), new GoalCard(), new GoalCard()
        );
        assertThrows(IllegalArgumentException.class, () -> {
            board.initializeGoalCards(fourCards);
        });
    }

    @Test
    void testGetAdjacentPositions() {
        BoardPosition center = board.getPosition(4, 2); // 보드 중앙 근처 위치
        List<BoardPosition> adjacent = board.getAdjacentPositions(center);
        
        assertEquals(4, adjacent.size()); // 상하좌우 4개의 인접 위치가 있어야 함
        
        // 경계에 있는 위치 테스트
        BoardPosition edge = board.getPosition(0, 0);
        List<BoardPosition> edgeAdjacent = board.getAdjacentPositions(edge);
        assertEquals(2, edgeAdjacent.size()); // 모서리에는 2개의 인접 위치만 있어야 함
    }

    @Test
    void testIsValidPosition() {
        assertTrue(board.getPosition(0, 0) != null);
        assertTrue(board.getPosition(8, 4) != null);
        assertNull(board.getPosition(-1, 0));
        assertNull(board.getPosition(0, -1));
        assertNull(board.getPosition(9, 0));
        assertNull(board.getPosition(0, 5));
    }

    @Test
    void testIsEmpty() {
        assertTrue(board.isEmpty());
        
        // 시작 카드 배치
        board.initializeGoalCards(Arrays.asList(new GoalCard(), new GoalCard(), new GoalCard()));
        
        // 시작 카드만 있는 경우에도 보드는 비어있다고 간주
        assertTrue(board.isEmpty());
        
        // 다른 카드 배치
        board.placeCard(3, 2, new GoalCard());
        assertFalse(board.isEmpty());
    }
} 