package com.goldstone.saboteur_backend.domain.user;

import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.game.GameCardPool;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserCardDeck {
    private User user;
    private List<Card> cards;
    private GameCardPool cardPool; // 카드풀을 직접 관리 (선택사항)

    // 카드 드로우(덱이 남아있을 때만)
    public boolean drawCard() {
        if (cardPool != null && !cardPool.isEmpty()) {
            cards.add(cardPool.drawCard());
            return true;
        }
        return false;
    }

    // 외부에서 카드를 추가할 때
    public void addCard(Card card) {
        cards.add(card);
    }

    // 카드 사용
    public boolean useCard(Card card) {
        if (cards.contains(card)) {
            cards.remove(card);
            return true;
        }
        return false;
    }

    // 카드 버림
    public boolean discardCard(Card card) {
        if (cards.contains(card)) {
            cards.remove(card);
            return true;
        }
        return false;
    }

    // 카드 보유 여부
    public boolean hasCard(Card card) {
        return cards.contains(card);
    }
}
