package com.goldstone.saboteur_backend.domain.board;

import com.goldstone.saboteur_backend.domain.card.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    private Board board;
    private GoalCard goalCard1;
    private GoalCard goalCard2;
    private GoalCard goalCard3;

    @BeforeEach
    void setUp() {
        board = new Board();
        goalCard1 = new GoalCard();  // 골드가 있는 목표 카드
        goalCard2 = new GoalCard(); // 골드가 없는 목표 카드
        goalCard3 = new GoalCard(); // 골드가 없는 목표 카드
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
        // 목표 카드 초기화
        board.initializeGoalCards(List.of(goalCard1, goalCard2, goalCard3));

        // 시작 카드가 올바른 위치에 있는지 확인
        BoardPosition startPosition = board.getStartPosition();
        assertNotNull(startPosition);
        assertTrue(startPosition.getCard() instanceof StartCard);
        assertTrue(startPosition.isRevealed());

        // 목표 카드들이 올바른 위치에 있는지 확인
        assertTrue(board.isGoalPosition(board.getPosition(8, 0))); // 첫 번째 목표 카드
        assertTrue(board.isGoalPosition(board.getPosition(8, 2))); // 두 번째 목표 카드
        assertTrue(board.isGoalPosition(board.getPosition(8, 4))); // 세 번째 목표 카드
    }

    @Test
    void testPlaceCard() {
        board.initializeGoalCards(List.of(goalCard1, goalCard2, goalCard3));

        // 시작 카드 옆에 경로 카드 배치
        Set<Direction> straightPath = EnumSet.of(Direction.LEFT, Direction.RIGHT);
        PathCard pathCard = new PathCard(straightPath);
        assertTrue(board.placeCard(1, 2, pathCard)); // 시작 카드 오른쪽에 배치

        // 이미 카드가 있는 위치에 배치 시도
        assertFalse(board.placeCard(1, 2, pathCard));

        // 유효하지 않은 위치에 배치 시도
        assertFalse(board.placeCard(-1, 2, pathCard));
        assertFalse(board.placeCard(1, -1, pathCard));
        assertFalse(board.placeCard(9, 2, pathCard));
        assertFalse(board.placeCard(1, 5, pathCard));
    }

    @Test
    void testPathConnection() {
        board.initializeGoalCards(List.of(goalCard1, goalCard2, goalCard3));

        // 직선 경로 카드 생성
        Set<Direction> straightPath = EnumSet.of(Direction.LEFT, Direction.RIGHT);
        PathCard pathCard1 = new PathCard(straightPath);
        PathCard pathCard2 = new PathCard(straightPath);

        // 시작 카드 오른쪽에 첫 번째 경로 카드 배치
        assertTrue(board.placeCard(1, 2, pathCard1));

        // 첫 번째 경로 카드 오른쪽에 두 번째 경로 카드 배치
        assertTrue(board.placeCard(2, 2, pathCard2));

        // 연결되지 않는 경로로 배치 시도
        Set<Direction> verticalPath = EnumSet.of(Direction.UP, Direction.DOWN);
        PathCard verticalCard = new PathCard(verticalPath);
        assertFalse(board.placeCard(3, 2, verticalCard)); // 수직 경로는 연결될 수 없음
    }

    @Test
    void testPathToGoal() {
        board.initializeGoalCards(List.of(goalCard1, goalCard2, goalCard3));

        // 시작 카드에서 목표 카드까지의 경로 생성
        Set<Direction> straightPath = EnumSet.of(Direction.LEFT, Direction.RIGHT);
        for (int x = 1; x < 8; x++) {
            PathCard pathCard = new PathCard(straightPath);
            assertTrue(board.placeCard(x, 2, pathCard));
        }

        // 경로가 목표 카드에 도달하는지 확인
        assertTrue(PathValidator.hasValidPathToGoal(board));
    }

    @Test
    void testInvalidPathToGoal() {
        board.initializeGoalCards(List.of(goalCard1, goalCard2, goalCard3));

        // 시작 카드 오른쪽에 경로 카드 배치
        Set<Direction> straightPath = EnumSet.of(Direction.LEFT, Direction.RIGHT);
        PathCard pathCard = new PathCard(straightPath);
        assertTrue(board.placeCard(1, 2, pathCard));

        // 경로가 목표 카드에 도달하지 않는지 확인
        assertFalse(PathValidator.hasValidPathToGoal(board));
    }

    @Test
    void testCardRotation() {
        board.initializeGoalCards(List.of(goalCard1, goalCard2, goalCard3));

        // T자 경로 카드 생성
        Set<Direction> tPath = EnumSet.of(Direction.UP, Direction.RIGHT, Direction.DOWN);
        PathCard tCard = new PathCard(tPath);

        // 카드 회전
        tCard.rotate();
        assertTrue(tCard.hasPath(Direction.DOWN));
        assertTrue(tCard.hasPath(Direction.LEFT));
        assertTrue(tCard.hasPath(Direction.UP));
        assertFalse(tCard.hasPath(Direction.RIGHT));
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
        board.initializeGoalCards(List.of(goalCard1, goalCard2, goalCard3));
        
        // 시작 카드만 있는 경우에도 보드는 비어있다고 간주
        assertTrue(board.isEmpty());
        
        // 다른 카드 배치
        board.placeCard(3, 2, new GoalCard());
        assertFalse(board.isEmpty());
    }
} 