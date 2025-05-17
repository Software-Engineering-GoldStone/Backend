package com.goldstone.saboteur_backend.domain.board;

import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.card.GoalCard;
import com.goldstone.saboteur_backend.domain.card.StartCard;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Board {
    private static final int BOARD_WIDTH = 9;
    private static final int BOARD_HEIGHT = 5;
    
    private final BoardPosition[][] positions;
    private final BoardPosition startPosition;
    private final List<BoardPosition> goalPositions;
    private boolean initialized;

    public Board() {
        this.positions = new BoardPosition[BOARD_HEIGHT][BOARD_WIDTH];
        this.goalPositions = new ArrayList<>();
        initializeBoard();
        this.startPosition = positions[BOARD_HEIGHT / 2][0]; // 시작 위치
        this.initialized = false;
    }

    private void initializeBoard() {
        // 보드의 모든 위치 초기화
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                positions[y][x] = new BoardPosition(x, y);
            }
        }
    }

    public void initializeGoalCards(List<GoalCard> goalCards) {
        if (initialized) {
            throw new IllegalStateException("Board is already initialized");
        }

        if (goalCards.size() != 3) {
            throw new IllegalArgumentException("Exactly 3 goal cards are required");
        }

        // 목표 카드 배치 (게임 시작 시 한 번만 실행)
        // y 좌표: 0, 2, 4 (첫 번째, 중간, 마지막 줄)
        int[] goalYPositions = {0, 2, 4};
        for (int i = 0; i < goalCards.size(); i++) {
            BoardPosition position = positions[goalYPositions[i]][BOARD_WIDTH - 1]; // x=8 (가장 오른쪽)
            position.placeCard(goalCards.get(i));
            goalPositions.add(position);
        }
        
        // 시작 카드(StartCard의 싱글톤 인스턴스) 배치
        startPosition.placeCard(StartCard.getInstance());
        startPosition.reveal();
        
        initialized = true;
    }

    public boolean isEmpty() {
        for (BoardPosition[] row : positions) {
            for (BoardPosition position : row) {
                if (!position.isEmpty() && position != startPosition) {
                    return false; 
                    //true : 보드가 비어있음 (시작 카드만 있거나 아무 카드도 없음)
                    //false: 보드에 시작 카드 외의 다른 카드가 하나라도 있음
                }
            }
        }
        return true;
    }

    public List<BoardPosition> getAdjacentPositions(BoardPosition position) {
        List<BoardPosition> adjacent = new ArrayList<>();
        int x = position.getX();
        int y = position.getY();

        // 상하좌우 위치 확인
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            if (isValidPosition(newX, newY)) {
                adjacent.add(positions[newY][newX]);
            }
        }

        return adjacent;
    }

    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < BOARD_WIDTH && y >= 0 && y < BOARD_HEIGHT;
    }

    public boolean isGoalPosition(BoardPosition position) {
        return goalPositions.contains(position);
    }

    public boolean placeCard(int x, int y, Card card) {
        if (!isValidPosition(x, y)) {
            return false;
        }

        BoardPosition position = positions[y][x];
        if (PathValidator.isValidPlacement(this, position, card)) {
            position.placeCard(card);
            return true;
        }
        return false;
    }

    public BoardPosition getPosition(int x, int y) {
        if (!isValidPosition(x, y)) {
            return null;
        }
        return positions[y][x];
    }
} 