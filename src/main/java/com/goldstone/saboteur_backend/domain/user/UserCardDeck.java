package com.goldstone.saboteur_backend.domain.user;

import com.goldstone.saboteur_backend.domain.card.Card;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserCardDeck {
    private User user;
<<<<<<< HEAD
    private List<Card> cards;
=======

    private List<Card> cards = new ArrayList<>();
>>>>>>> develop

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
