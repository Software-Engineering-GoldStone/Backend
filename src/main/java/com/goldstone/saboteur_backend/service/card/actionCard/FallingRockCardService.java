package com.goldstone.saboteur_backend.service.card.actionCard;

import com.goldstone.saboteur_backend.domain.board.Board;
import com.goldstone.saboteur_backend.domain.board.Cell;
import com.goldstone.saboteur_backend.domain.card.actionCard.FallingRockCard;
import com.goldstone.saboteur_backend.dtos.card.response.UseCardResponse;
import com.goldstone.saboteur_backend.session.GlobalSession;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FallingRockCardService {
    private final GlobalSession globalSession;

    public UseCardResponse use(FallingRockCard card, UUID roomId, int x, int y) {
        Board board = globalSession.getGameBoardSession(roomId);
        Cell cell = board.getOrCreateCell(x, y);

        card.use(cell);

        return new UseCardResponse(
                null, null, null, String.format("(%d, %d) 칸의 카드가 낙석으로 제거되었습니다.", x, y));
    }
}
