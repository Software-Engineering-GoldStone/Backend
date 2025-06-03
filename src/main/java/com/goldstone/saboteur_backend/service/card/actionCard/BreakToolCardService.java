package com.goldstone.saboteur_backend.service.card.actionCard;

import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.card.actionCard.BreakToolCard;
import com.goldstone.saboteur_backend.domain.enums.TargetToolType;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.dtos.card.request.UserTargetRequest;
import com.goldstone.saboteur_backend.dtos.card.response.UseCardResponse;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.CardErrorCode;
import com.goldstone.saboteur_backend.session.GlobalSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BreakToolCardService {

    private final GlobalSession globalSession;

    public UseCardResponse use(UserTargetRequest request) {
        User user = globalSession.getUserSession(request.getUserId());
        User targetUser = globalSession.getUserSession(request.getTargetUserId());

        Card card =
                user.getCardDeck()
                        .getCardById(request.getCardId())
                        .orElseThrow(() -> new BusinessException(CardErrorCode.INVALID_CARD_ID));

        if (!(card instanceof BreakToolCard)) {
            throw new BusinessException(CardErrorCode.INVALID_CARD_TYPE);
        }

        ((BreakToolCard) card).use(targetUser);

        TargetToolType targetToolType = ((BreakToolCard) card).getTargetTool();

        return new UseCardResponse(
                targetUser.getId(),
                targetToolType,
                targetUser.getToolStatusMap().get(targetToolType),
                targetUser.getNickname() + "의 도구가 파괴되었습니다.");
    }
}
