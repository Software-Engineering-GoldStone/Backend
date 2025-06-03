package com.goldstone.saboteur_backend.service.card.actionCard;

import com.goldstone.saboteur_backend.domain.board.Board;
import com.goldstone.saboteur_backend.domain.board.Cell;
import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.card.actionCard.FallingRockCard;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.dtos.card.request.CellTargetCardRequest;
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

    public UseCardResponse use(CellTargetCardRequest request) {
        User user = globalSession.getUserSession(request.getUserId());
        Card card =
                user.getCardDeck()
                        .getCardById(request.getCardId())
                        .orElseThrow(() -> new BusinessException(CardErrorCode.INVALID_CARD_ID));

        if (!(card instanceof FallingRockCard)) {
            throw new BusinessException(CardErrorCode.INVALID_CARD_TYPE);
        }

        Board board = globalSession.getGameBoardSession(request.getRoomId());

        int x = request.getTargetCellX();
        int y = request.getTargetCellY();

        Cell cell = board.getOrCreateCell(x, y);

        ((FallingRockCard) card).use(cell);

        return new UseCardResponse(
                null, null, null, String.format("(%d, %d) 칸의 카드가 낙석으로 제거되었습니다.", x, y));
    }
}
