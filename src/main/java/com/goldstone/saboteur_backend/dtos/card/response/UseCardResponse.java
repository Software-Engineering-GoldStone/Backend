package com.goldstone.saboteur_backend.dtos.card.response;

import com.goldstone.saboteur_backend.domain.enums.PlayerToolStatus;
import com.goldstone.saboteur_backend.domain.enums.TargetToolType;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UseCardResponse {
    private UUID affectedUserId;
    private TargetToolType affectedTool;
    private PlayerToolStatus newStatus;
    private String message;
}
