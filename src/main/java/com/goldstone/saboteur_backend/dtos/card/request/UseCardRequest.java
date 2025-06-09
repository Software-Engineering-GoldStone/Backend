package com.goldstone.saboteur_backend.dtos.card.request;

import com.goldstone.saboteur_backend.domain.enums.CardType;
import java.util.UUID;

public interface UseCardRequest {
    UUID getUserId();

    UUID getCardId();

    UUID getRoomId();

    CardType getCardType();

    UUID getGameRoomId();
}
