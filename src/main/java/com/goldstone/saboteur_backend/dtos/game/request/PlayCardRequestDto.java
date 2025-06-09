package com.goldstone.saboteur_backend.dtos.game.request;

import java.util.UUID;

import com.goldstone.saboteur_backend.domain.enums.CardType;
import com.goldstone.saboteur_backend.domain.enums.TargetToolType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class PlayCardRequestDto {
    private UUID userId;
    private UUID cardId;
    private UUID gameRoomId;

    // Repair, Destroy Card
    private TargetToolType targetTool;
    private UUID targetUserID;

    // Map, Falling Rock, Path Card
    private int x;
    private int y;
}
