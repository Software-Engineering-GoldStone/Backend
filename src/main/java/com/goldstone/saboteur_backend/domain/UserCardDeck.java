package com.goldstone.saboteur_backend.domain;

import com.goldstone.saboteur_backend.domain.card.Card;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserCardDeck {
    private User user;

    private List<Card> cards;

    public boolean hasCard(Card card) {
        return cards.contains(card);
    }
}
