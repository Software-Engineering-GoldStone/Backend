package com.goldstone.saboteur_backend.service.card.actionCard;

import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.card.actionCard.*;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.dtos.card.request.CellTargetCardRequest;
import com.goldstone.saboteur_backend.dtos.card.request.UseCardRequest;
import com.goldstone.saboteur_backend.dtos.card.request.UserTargetRequest;
import com.goldstone.saboteur_backend.dtos.card.response.UseCardResponse;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.CardErrorCode;
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

        Card card =
                user.getCardDeck().getCards().stream()
                        .filter(c -> c.getId().equals(request.getCardId()))
                        .findFirst()
                        .orElseThrow(() -> new BusinessException(CardErrorCode.INVALID_CARD_ID));

        ActionCard actionCard = (ActionCard) card;

        UseCardResponse response;

        switch (actionCard.getActionCardType()) {
            case DESTROY -> {
                if (!(request instanceof UserTargetRequest breakReq)) {
                    throw new BusinessException(CardErrorCode.INVALID_ACTION_CARD);
                }
                response = breakToolCardService.use(breakReq);
            }
            case REPAIR -> {
                if (!(request instanceof UserTargetRequest repairReq)) {
                    throw new BusinessException(CardErrorCode.INVALID_ACTION_CARD);
                }
                response = repairToolCardService.use(repairReq);
            }
            case MAP -> {
                if (!(request instanceof CellTargetCardRequest mapReq)) {
                    throw new BusinessException(CardErrorCode.INVALID_ACTION_CARD);
                }
                response = mapCardService.use(mapReq);
            }
            case FALLING_ROCK -> {
                if (!(request instanceof CellTargetCardRequest rockReq)) {
                    throw new BusinessException(CardErrorCode.INVALID_ACTION_CARD);
                }
                response = fallingRockCardService.use(rockReq);
            }
            default -> throw new BusinessException(CardErrorCode.INVALID_CARD_TYPE);
        }


        user.getCardDeck().useCard(actionCard);

        return response;
    }
}
