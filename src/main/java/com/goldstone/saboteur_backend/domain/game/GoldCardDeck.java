package com.goldstone.saboteur_backend.domain.game;

import com.goldstone.saboteur_backend.domain.card.GoldCard;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class GoldCardDeck {
    private Queue<GoldCard> goldCards = new LinkedList<>();

    public GoldCardDeck() {
        List<GoldCard> goldList = new ArrayList<>();
        for (int i = 0; i < 16; i++) goldList.add(new GoldCard(1));
        for (int i = 0; i < 8; i++) goldList.add(new GoldCard(2));
        for (int i = 0; i < 4; i++) goldList.add(new GoldCard(3));
        Collections.shuffle(goldList);
        goldCards = new LinkedList<>(goldList);
    }

    public List<GoldCard> drawGoldCards(int count) {
        List<GoldCard> result = new ArrayList<>();
        for (int i = 0; i < count && !goldCards.isEmpty(); i++) {
            result.add(goldCards.poll());
        }
        return result;
    }
}
