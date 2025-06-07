package com.goldstone.saboteur_backend.domain.user;

import com.goldstone.saboteur_backend.domain.card.Card;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserCardDeck {
    private User user;

    private List<Card> cards = new ArrayList<>();

    public boolean hasCard(Card card) {
        return cards.contains(card);
    }

    public boolean useCard(Card card) {
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).equals(card)) {
                cards.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        return cards == null || cards.isEmpty();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public Optional<Card> getCardById(UUID cardId) {
        return cards.stream().filter(card -> card.getCardId().equals(cardId)).findFirst();
    }
}
