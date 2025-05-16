package com.goldstone.saboteur_backend.domain.game;

import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.CardErrorCode;
import java.util.LinkedList;
import java.util.Queue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GameCardPool {
    private Queue<Card> cards = new LinkedList<>();

    public boolean isEmpty() {
        return cards == null || cards.isEmpty();
    }

    public Card drawCard() {
        if (cards == null || cards.isEmpty()) {
            throw new BusinessException(CardErrorCode.NO_CARDS_LEFT);
        }
        return cards.poll();
    }
}
