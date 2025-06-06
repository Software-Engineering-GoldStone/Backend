package com.goldstone.saboteur_backend.service.board;

import com.goldstone.saboteur_backend.domain.board.Board;
import com.goldstone.saboteur_backend.domain.board.Cell;
import com.goldstone.saboteur_backend.domain.card.GoalCard;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.GameRoomErrorCode;
import com.goldstone.saboteur_backend.session.GlobalSession;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final GlobalSession globalSession;

    public List<Cell> isReachableGoal(UUID gameRoomId) {
        GameRoom gameRoom = this.globalSession.getGameRoomSession(gameRoomId);
        if (gameRoom == null) {
            throw new BusinessException(GameRoomErrorCode.GAME_ROOM_NOT_FOUND);
        }
        Board board = this.globalSession.getGameBoardSession(gameRoomId);
        if (board == null) {
            throw new BusinessException(GameRoomErrorCode.GAME_BOARD_NOT_FOUND);
        }

        int dx[] = {0, 1, 0, -1};
        int dy[] = {1, 0, -1, 0};

        List<Cell> result = new ArrayList<>();

        int size = board.getSize();
        boolean[][] visited = new boolean[size + 1][size + 1];

        Queue<Cell> queue = new LinkedList<>();
        Cell startCell = board.getCellFromXAndY(0, Board.DEFAULT_HEIGHT / 2);

        queue.add(startCell);
        visited[startCell.getX()][startCell.getY()] = true;

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
                if (visited[nx][ny]) continue;

                visited[nx][ny] = true;
                queue.add(nextCell);
            }
        }

        return result;
    }
}
