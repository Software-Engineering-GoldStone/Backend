package com.goldstone.saboteur_backend.service.card.actionCard;

import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.card.actionCard.RepairToolCard;
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
public class RepairToolCardService {
    private final GlobalSession globalSession;

    public UseCardResponse use(UserTargetRequest request) {
        User user = globalSession.getUserSession(request.getUserId());
        User targetUser = globalSession.getUserSession(request.getTargetUserId());

        Card card =
                user.getCardDeck()
                        .getCardById(request.getCardId())
                        .orElseThrow(() -> new BusinessException(CardErrorCode.INVALID_CARD_ID));

        if (!(card instanceof RepairToolCard)) {
            throw new BusinessException(CardErrorCode.INVALID_CARD_TYPE);
        }

        ((RepairToolCard) card).selectTool(request.getSelectedTool());
        ((RepairToolCard) card).use(targetUser);

        return new UseCardResponse(
                targetUser.getId(),
                request.getSelectedTool(),
                targetUser.getToolStatusMap().get(request.getSelectedTool()),
                targetUser.getNickname()
                        + "의 "
                        + request.getSelectedTool().name()
                        + "이(가) 복구되었습니다.");
    }
}
