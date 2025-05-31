package com.goldstone.saboteur_backend.service.card.actionCard;

import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.dtos.card.request.UseCardRequest;
import com.goldstone.saboteur_backend.dtos.card.response.UseCardResponse;
import com.goldstone.saboteur_backend.session.GlobalSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActionCardService {
    private final BreakToolCardService breakToolCardService;
    private final RepairToolCardService repairToolCardService;
    private final MapCardService mapCardService;
    private final FallingRockCardService fallingRockCardService;

    private final GlobalSession globalSession;

    public UseCardResponse useActionCard(UseCardRequest request) {
        User user = globalSession.getUserSession(request.getUserId());
        Card card = user.getCardDeck().getCardById(request.getCardId());

        ActionCard actionCard = (ActionCard) card;

        UseCardResponse response =
                switch (actionCard.getActionCardType()) {
                    case DESTROY ->
                            breakToolCardService.use(
                                    (BreakToolCard) actionCard, request.getTargetUserId());
                    case REPAIR ->
                            repairToolCardService.use(
                                    (RepairToolCard) actionCard,
                                    request.getTargetUserId(),
                                    request.getSelectedTool());
                    case MAP ->
                            mapCardService.use(
                                    (MapCard) actionCard,
                                    request.getRoomId(),
                                    request.getTargetCellX(),
                                    request.getTargetCellY());
                    case FALLING_ROCK ->
                            fallingRockCardService.use(
                                    (FallingRockCard) actionCard,
                                    request.getRoomId(),
                                    request.getTargetCellX(),
                                    request.getTargetCellY());
                };

        user.getCardDeck().useCard(actionCard);
        return response;
    }
}
