package com.goldstone.saboteur_backend.service.board;

import static org.junit.jupiter.api.Assertions.*;

import com.goldstone.saboteur_backend.domain.board.Board;
import com.goldstone.saboteur_backend.domain.board.Cell;
import com.goldstone.saboteur_backend.domain.card.GoalCard;
import com.goldstone.saboteur_backend.domain.card.PathCard;
import com.goldstone.saboteur_backend.domain.enums.GoalCardType;
import com.goldstone.saboteur_backend.domain.enums.PathCardType;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.session.GlobalSession;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BoardServiceTest {

    @Autowired private GlobalSession globalSession;
    @Autowired private BoardService boardService;

    @BeforeEach
    void setUp() {}

    @AfterEach
    void tearDown() {}

    @Test
    void isReachableGoal() {
        Board board = new Board();
        // 테스트를 위해 랜덤으로 배치된 골 카드를, 모든 방향이 PATH인 카드로 설정
        for (int i=0; i<Board.DEFAULT_GOAL_CELL_Y_LIST.length; i++) {
            int y = Board.DEFAULT_GOAL_CELL_Y_LIST[i];
            board.getCellFromXAndY(Board.DEFAULT_WIDTH - 1, y)
                    .setCard(new GoalCard(GoalCardType.GOLD, PathCardType.CROSSROAD));
        }

        UUID roomId = UUID.randomUUID();
        GameRoom gameRoom = new GameRoom();
        gameRoom.setId(roomId);

        PathCard[] pathCards = new PathCard[7];

        for (int i = 0; i < 7; i++) {
            pathCards[i] = new PathCard(PathCardType.CROSSROAD, false);

            Cell cell = board.getOrCreateCell(i + 1, Board.DEFAULT_HEIGHT / 2);
            cell.setCard(pathCards[i]);
        }

        this.globalSession.addGameRoomSession(gameRoom);
        this.globalSession.addGameBoardSession(gameRoom, board);

        List<Cell> result = this.boardService.isReachableGoal(gameRoom.getId());

        assertEquals(1, result.size());
        assertTrue(result.get(0).getCard() instanceof GoalCard);
        assertEquals(8, result.get(0).getX());
        assertEquals(2, result.get(0).getY());
    }
}
