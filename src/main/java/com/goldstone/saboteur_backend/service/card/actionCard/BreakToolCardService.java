package com.goldstone.saboteur_backend.service.card.actionCard;

import com.goldstone.saboteur_backend.domain.card.actionCard.BreakToolCard;
import com.goldstone.saboteur_backend.domain.enums.TargetToolType;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.dtos.card.response.UseCardResponse;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.CommonErrorCode;
import com.goldstone.saboteur_backend.session.GlobalSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BreakToolCardService {

    private final GlobalSession globalSession;

    public UseCardResponse use(User targetUser, BreakToolCard card) {
        if (targetUser == null) {
            System.out.println("targetUser is null in BreakToolCardService.java");
            throw new BusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }

        card.use(targetUser);

        TargetToolType targetToolType = card.getTargetTool();

        return new UseCardResponse(
                targetUser.getId(),
                targetToolType,
                targetUser.getToolStatusMap().get(targetToolType),
                targetUser.getNickname() + "의 도구가 파괴되었습니다.");
    }
}
