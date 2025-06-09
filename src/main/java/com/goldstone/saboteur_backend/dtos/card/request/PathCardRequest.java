package com.goldstone.saboteur_backend.dtos.card.request;

import com.goldstone.saboteur_backend.domain.enums.CardType;
import com.goldstone.saboteur_backend.domain.enums.PathCardType;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PathCardRequest implements UseCardRequest {
    private UUID userId;
    private UUID cardId;
    private UUID roomId;
    private int targetCellX;
    private int targetCellY;
    private boolean rotated;
    private CardType cardType;
    private PathCardType pathCardType;

    @Override
    public UUID getGameRoomId() {
        return this.roomId;
    }
}
