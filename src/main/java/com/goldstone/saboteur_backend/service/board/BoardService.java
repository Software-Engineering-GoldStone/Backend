package com.goldstone.saboteur_backend.service.board;

import com.goldstone.saboteur_backend.domain.board.Board;
import com.goldstone.saboteur_backend.domain.board.Cell;
import com.goldstone.saboteur_backend.domain.card.GoalCard;
import com.goldstone.saboteur_backend.session.GlobalSession;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final GlobalSession globalSession;

    public List<Cell> getReachableGoals(Board board) {
        int dx[] = {0, 1, 0, -1};
        int dy[] = {1, 0, -1, 0};

        List<Cell> result = new ArrayList<>();

        int size = board.getSize();
        Set<Cell> visited = new HashSet<>();

        Queue<Cell> queue = new LinkedList<>();
        Cell startCell = board.getCellFromXAndY(0, Board.DEFAULT_HEIGHT / 2);

        queue.add(startCell);
        visited.add(startCell);

        while (!queue.isEmpty()) {
            Cell currentCell = queue.poll();

            if (currentCell.getCard() instanceof GoalCard) {
                result.add(currentCell);
                continue;
            }

            int x = currentCell.getX();
            int y = currentCell.getY();

            for (int i = 0; i < 4; i++) {
                int nx = x + dx[i];
                int ny = y + dy[i];

                Cell nextCell = board.getCellFromXAndY(nx, ny);
                if (nextCell == null) continue;

                // 현재 셀이 골 카드이지만 셀 간 연결이 되지 않는 경우, 골 카드를 회전시켜서 한번 더 검증할 수 있도록 한다.
                if (nextCell.getCard() instanceof GoalCard goalCard
                        && !board.isConnected(currentCell, nextCell)) {
                    goalCard.rotate();
                }

                if (!board.isConnected(currentCell, nextCell)) continue;
                if (visited.contains(new Cell(nx, ny))) continue;

                visited.add(new Cell(nx, ny));
                queue.add(nextCell);
            }
        }

        return result;
    }
}
