package com.goldstone.saboteur_backend.domain.game;

import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.CardErrorCode;
import com.goldstone.saboteur_backend.exception.code.error.CardPoolErrorCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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

    public void shuffleCards() {
        if (cards.isEmpty()) {
            throw new BusinessException(CardPoolErrorCode.NO_CARDS_EXIST);
        }
        try {
            List<Card> cardList = new ArrayList<>(cards); // Queue -> List
            Collections.shuffle(cardList);
            cards = new LinkedList<>(cardList); // List -> Queue
        } catch (Exception e) {
            throw new BusinessException(CardPoolErrorCode.SHUFFLE_ERROR);
        }
    }

    public void assignCards(List<User> users, int cardsPerPlayer) {
        if (cards.isEmpty()) {
            throw new BusinessException(CardPoolErrorCode.NO_CARDS_EXIST);
        }
        for (User user : users) {
            List<Card> cards = new ArrayList<>();
            for (int i = 0; i < cardsPerPlayer; i++) {
                cards.add(this.drawCard());
            }
            if (user.getCardDeck() != null && user.getCardDeck().getCards() != null) {
                user.getCardDeck().getCards().addAll(cards);
            }
        }
    }
}
