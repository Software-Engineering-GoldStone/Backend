package com.goldstone.saboteur_backend.dtos.card.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public abstract class CardInfoResponseDto {
    protected String id;
}
