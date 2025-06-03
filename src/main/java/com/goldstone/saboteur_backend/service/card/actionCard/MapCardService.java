package com.goldstone.saboteur_backend.service.card.actionCard;

import com.goldstone.saboteur_backend.domain.board.Board;
import com.goldstone.saboteur_backend.domain.board.Cell;
import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.card.actionCard.MapCard;
import com.goldstone.saboteur_backend.domain.enums.GoalCardType;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.dtos.card.request.CellTargetCardRequest;
import com.goldstone.saboteur_backend.dtos.card.response.UseCardResponse;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.CardErrorCode;
import com.goldstone.saboteur_backend.session.GlobalSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MapCardService {
    private final GlobalSession globalSession;

    public UseCardResponse use(CellTargetCardRequest request) {
        User user = globalSession.getUserSession(request.getUserId());
        Card card =
                user.getCardDeck()
                        .getCardById(request.getCardId())
                        .orElseThrow(() -> new BusinessException(CardErrorCode.INVALID_CARD_ID));

        if (!(card instanceof MapCard)) {
            throw new BusinessException(CardErrorCode.INVALID_CARD_TYPE);
        }

        Board board = globalSession.getGameBoardSession(request.getRoomId());

        int x = request.getTargetCellX();
        int y = request.getTargetCellY();

        Cell cell = board.getOrCreateCell(x, y);

        GoalCardType goalCardType = ((MapCard) card).peekDestinationCard(cell);

        return new UseCardResponse(null, null, null, "선택한 목적지 카드는" + goalCardType.name() + "입니다.");
    }
}
