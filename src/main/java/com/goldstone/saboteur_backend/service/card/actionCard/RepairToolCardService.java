package com.goldstone.saboteur_backend.service.card.actionCard;

import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.card.actionCard.RepairToolCard;
import com.goldstone.saboteur_backend.domain.enums.TargetToolType;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.dtos.card.request.UserTargetRequest;
import com.goldstone.saboteur_backend.dtos.card.response.UseCardResponse;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.CardErrorCode;
import com.goldstone.saboteur_backend.exception.code.error.CommonErrorCode;
import com.goldstone.saboteur_backend.exception.responseDto.ErrorResponse;
import com.goldstone.saboteur_backend.session.GlobalSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RepairToolCardService {
    private final GlobalSession globalSession;

    public UseCardResponse use(User targetUser, RepairToolCard card, TargetToolType targetTool) {
        if (targetUser == null || targetTool == null) {
            System.out.println("targetUser or targetTool is null in RepairToolCardService.java");
            System.out.println(targetUser);
            System.out.println(targetTool);
            throw new BusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }

        card.selectTool(targetTool);
        card.use(targetUser);

        return new UseCardResponse(
                targetUser.getId(),
                targetTool,
                targetUser.getToolStatusMap().get(targetTool),
                targetUser.getNickname()
                        + "의 "
                        + targetTool.name()
                        + "이(가) 복구되었습니다.");
    }
}
