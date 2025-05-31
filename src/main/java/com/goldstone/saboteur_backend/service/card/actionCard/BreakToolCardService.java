package com.goldstone.saboteur_backend.service.card.actionCard;

import com.goldstone.saboteur_backend.domain.card.actionCard.BreakToolCard;
import com.goldstone.saboteur_backend.domain.enums.TargetToolType;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.dtos.card.response.UseCardResponse;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.UserErrorCode;
import com.goldstone.saboteur_backend.session.GlobalSession;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BreakToolCardService {

    private final GlobalSession globalSession;

    public UseCardResponse use(BreakToolCard card, UUID targetUserId) {
        User target = globalSession.getUserSession(targetUserId);

        if (target == null) {
            throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
        }

        card.use(target);

        TargetToolType targetToolType = card.getTargetTool();

        return new UseCardResponse(
                target.getId(),
                targetToolType,
                target.getToolStatusMap().get(targetToolType),
                target.getNickname() + "의 도구가 복구되었습니다.");
    }
}
