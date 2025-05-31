package com.goldstone.saboteur_backend.service.card.actionCard;

import com.goldstone.saboteur_backend.domain.card.actionCard.RepairToolCard;
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
public class RepairToolCardService {
    private final GlobalSession globalSession;

    public UseCardResponse use(
            RepairToolCard card, UUID targetUserId, TargetToolType selectedTool) {
        User target = globalSession.getUserSession(targetUserId);

        if (target == null) {
            throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
        }

        card.selectTool(selectedTool);

        card.use(target);

        return new UseCardResponse(
                target.getId(),
                selectedTool,
                target.getToolStatusMap().get(selectedTool),
                target.getNickname() + "의 " + selectedTool.name() + "이(가) 복구되었습니다.");
    }
}
