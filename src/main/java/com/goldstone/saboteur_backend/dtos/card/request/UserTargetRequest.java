package com.goldstone.saboteur_backend.dtos.card.request;

import com.goldstone.saboteur_backend.domain.enums.ActionCardType;
import com.goldstone.saboteur_backend.domain.enums.CardType;
import com.goldstone.saboteur_backend.domain.enums.TargetToolType;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserTargetRequest implements UseCardRequest {
    private UUID userId;
    private UUID cardId;
    private UUID roomId;
    private UUID targetUserId;
    private CardType cardType;
    private ActionCardType actionCardType;
    private TargetToolType selectedTool;
}
