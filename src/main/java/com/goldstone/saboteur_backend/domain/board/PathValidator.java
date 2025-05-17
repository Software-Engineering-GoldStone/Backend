package com.goldstone.saboteur_backend.domain.board;

import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.card.Direction;
import java.util.*;

public class PathValidator {
    
    public static boolean isValidPlacement(Board board, BoardPosition position, Card card) {
        // 이미 카드가 있는 위치인지 확인
        if (!position.isEmpty()) {
            return false;
        }

        // 첫 카드 배치의 경우 (시작 카드 주변)
        if (board.isEmpty()) {
            return isAdjacentToStart(position);
        }

        // 인접한 카드가 있는지 확인
        List<BoardPosition> adjacentPositions = board.getAdjacentPositions(position);
        boolean hasAdjacentCard = false;
        
        for (BoardPosition adjacent : adjacentPositions) {
            if (!adjacent.isEmpty()) {
                hasAdjacentCard = true;
                // 경로가 연결되는지(연결 가능한지) 확인
                if (!arePathsConnected(adjacent, position, card)) {
                    return false;
                }
            }
        }

        return hasAdjacentCard;
    }

    private static boolean isAdjacentToStart(BoardPosition position) {
        int x = position.getX();
        int y = position.getY();
        
        // 시작 카드의 위치
        int startX = 0;
        int startY = 2;
        
        // 시작 카드의 오른쪽, 위, 아래에만 카드 배치 가능
        return (x == startX + 1 && y == startY) || // 오른쪽
               (x == startX && y == startY - 1) || // 위
               (x == startX && y == startY + 1);   // 아래
    }

    private static boolean arePathsConnected(BoardPosition existing, BoardPosition target, Card newCard) {
        Card existingCard = existing.getCard();
        if (existingCard == null || newCard == null) {
            return false;
        }

        // 방향 : existing 기준 어느쪽인지
        Direction direction = getDirectionBetweenPositions(existing, target);
        if (direction == null) {
            return false;
        }

        // 두 카드가 서로 연결 가능한지 확인
        return existingCard.canConnectTo(newCard, direction) && 
               newCard.canConnectTo(existingCard, direction.getOpposite());
    }

    private static Direction getDirectionBetweenPositions(BoardPosition from, BoardPosition to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();

        if (dx == 1 && dy == 0) return Direction.RIGHT;
        if (dx == -1 && dy == 0) return Direction.LEFT;
        if (dx == 0 && dy == 1) return Direction.DOWN;
        if (dx == 0 && dy == -1) return Direction.UP;

        return null; // 대각선이나 더 먼 거리는 연결 불가
    }

    public static boolean hasValidPathToGoal(Board board) {
        // 시작점에서 목표 카드까지의 유효한 경로가 있는지 확인
        BoardPosition start = board.getStartPosition();
        Set<BoardPosition> visited = new HashSet<>();
        return findPathToGoal(board, start, visited);
    }

    private static boolean findPathToGoal(Board board, BoardPosition current, Set<BoardPosition> visited) {
        if (current == null || visited.contains(current) || current.isEmpty()) {
            return false;
        }

        if (board.isGoalPosition(current)) {
            return true;
        }

        visited.add(current);
        List<BoardPosition> adjacentPositions = board.getAdjacentPositions(current);

        for (BoardPosition next : adjacentPositions) {
            if (next != null && !visited.contains(next) && !next.isEmpty()) {
                if (arePathsConnected(current, next, next.getCard())) {
                    if (findPathToGoal(board, next, visited)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
} 