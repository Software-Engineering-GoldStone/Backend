package com.goldstone.saboteur_backend.service.card.actionCard;

import com.goldstone.saboteur_backend.domain.board.Board;
import com.goldstone.saboteur_backend.domain.board.Cell;
import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.card.GoalCard;
import com.goldstone.saboteur_backend.domain.card.StartCard;
import com.goldstone.saboteur_backend.domain.card.actionCard.FallingRockCard;
import com.goldstone.saboteur_backend.dtos.card.response.UseCardResponse;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.CardErrorCode;
import com.goldstone.saboteur_backend.session.GlobalSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FallingRockCardService {
    private final GlobalSession globalSession;

    public UseCardResponse use(Board board, FallingRockCard card, int x, int y) {
        Cell cell = board.getOrCreateCell(x, y);

        Card targetCard = cell.getCard();

        if (targetCard == null
                || targetCard instanceof GoalCard
                || targetCard instanceof StartCard) {
            throw new BusinessException(CardErrorCode.INVALID_ACTION_CARD);
        }

        card.use(cell);

        return new UseCardResponse(
                null, null, null, String.format("(%d, %d) 칸의 카드가 낙석으로 제거되었습니다.", x, y));
    }
}
