package com.goldstone.saboteur_backend.service.card.actionCard;

import com.goldstone.saboteur_backend.domain.board.Board;
import com.goldstone.saboteur_backend.domain.board.Cell;
import com.goldstone.saboteur_backend.domain.card.actionCard.MapCard;
import com.goldstone.saboteur_backend.domain.enums.GoalCardType;
import com.goldstone.saboteur_backend.dtos.card.response.UseCardResponse;
import com.goldstone.saboteur_backend.session.GlobalSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MapCardService {
    private final GlobalSession globalSession;

    public UseCardResponse use(Board board, MapCard card, int x, int y) {

        Cell cell = board.getOrCreateCell(x, y);
        GoalCardType goalCardType = card.peekDestinationCard(cell);

        return new UseCardResponse(null, null, null, "선택한 목적지 카드는" + goalCardType.name() + "입니다.");
    }
}
