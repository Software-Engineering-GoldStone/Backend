package com.goldstone.saboteur_backend.service.board;

import com.goldstone.saboteur_backend.domain.board.Board;
import com.goldstone.saboteur_backend.domain.board.Cell;
import com.goldstone.saboteur_backend.domain.card.GoalCard;
import com.goldstone.saboteur_backend.domain.card.PathCard;
import com.goldstone.saboteur_backend.domain.enums.PathCardType;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.session.GlobalSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BoardServiceTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void isReachableGoal() {
        Board board = new Board();
        UUID roomId = UUID.randomUUID();
        GameRoom gameRoom = new GameRoom();
        gameRoom.setId(roomId);

        GlobalSession globalSession = new GlobalSession();

        PathCard[] pathCards = new PathCard[7];

        for(int i=0; i<7; i++){
            pathCards[i] = new PathCard(PathCardType.CROSSROAD, false);

            Cell cell = board.getOrCreateCell(i+1,2);
            cell.setCard(pathCards[i]);
        }

        globalSession.addGameRoomSession(gameRoom);
        globalSession.addGameBoardSession(gameRoom, board);

        BoardService boardService = new BoardService(globalSession);
        List<Cell> result = boardService.isReachableGoal(gameRoom.getId());

        assertEquals(1, result.size());
        assertTrue(result.get(0).getCard() instanceof GoalCard);
        assertEquals(8, result.get(0).getX());
        assertEquals(2, result.get(0).getY());

    }
}