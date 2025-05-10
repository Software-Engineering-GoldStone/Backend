package com.goldstone.saboteur_backend.domain.card;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GoldCard extends Card {
    private Integer amount;

    @Override
    public void use() {}

    @Override
    public boolean availableUse() {
        return false;
    }
}
