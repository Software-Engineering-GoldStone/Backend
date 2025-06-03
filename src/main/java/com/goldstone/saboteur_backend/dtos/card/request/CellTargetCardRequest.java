package com.goldstone.saboteur_backend.dtos.card.request;

import com.goldstone.saboteur_backend.domain.enums.CardType;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CellTargetCardRequest implements UseCardRequest {
    private UUID userId;
    private UUID cardId;
    private UUID roomId;
    private CardType cardType;
    private int targetCellX;
    private int targetCellY;
}
