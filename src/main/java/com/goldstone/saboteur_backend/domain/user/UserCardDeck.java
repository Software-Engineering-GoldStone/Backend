package com.goldstone.saboteur_backend.domain.user;

import com.goldstone.saboteur_backend.domain.card.Card;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserCardDeck {
    private User user;
    private List<Card> cards;

    public boolean hasCard(Card card) {
        return cards.contains(card);
    }

    public boolean useCard(Card card) {
        if (cards.contains(card)) {
            cards.remove(card);
            return true;
        }
        return false;
    }

    public void addCard(Card card) {
        cards.add(card);
    }
}
