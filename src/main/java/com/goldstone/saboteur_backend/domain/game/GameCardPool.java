package com.goldstone.saboteur_backend.domain.game;

import com.goldstone.saboteur_backend.domain.card.ActionCard.BreakToolCard;
import com.goldstone.saboteur_backend.domain.card.ActionCard.FallingRockCard;
import com.goldstone.saboteur_backend.domain.card.ActionCard.MapCard;
import com.goldstone.saboteur_backend.domain.card.ActionCard.RepairToolCard;
import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.card.PathCard;
import com.goldstone.saboteur_backend.domain.enums.PathCardType;
import com.goldstone.saboteur_backend.domain.enums.TargetToolType;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class GameCardPool {
    private UUID id;
    private Queue<Card> cards = new LinkedList<>();

    public boolean isEmpty() {
        return cards == null || cards.isEmpty();
    }

    public Card drawCard() {
        if (cards == null || cards.isEmpty()) {
            throw new BusinessException(CardPoolErrorCode.NO_CARDS_EXIST);
        }
        return cards.poll();
    }

    /** 사보타지 공식 룰에 따라 카드풀을 생성한다. (길카드 44장, 행동카드 27장) */
    public static GameCardPool createDefaultPool(UUID poolId) {
        List<Card> cardList = new LinkedList<>();

        // 길카드 (예시: CROSSROAD 44장, 실제로는 다양한 PathCardType을 추가해야 함)
        for (int i = 0; i < 44; i++) {
            cardList.add(new PathCard(PathCardType.CROSSROAD, false));
        }

        // 도구 파괴 (각 3장씩)
        for (int i = 0; i < 3; i++) cardList.add(new BreakToolCard(TargetToolType.PICKAX));
        for (int i = 0; i < 3; i++) cardList.add(new BreakToolCard(TargetToolType.CART));
        for (int i = 0; i < 3; i++) cardList.add(new BreakToolCard(TargetToolType.LIGHT));

        // 도구 수리 (각 2장씩)
        for (int i = 0; i < 2; i++) cardList.add(new RepairToolCard(Set.of(TargetToolType.PICKAX)));
        for (int i = 0; i < 2; i++) cardList.add(new RepairToolCard(Set.of(TargetToolType.CART)));
        for (int i = 0; i < 2; i++) cardList.add(new RepairToolCard(Set.of(TargetToolType.LIGHT)));
        // 도구 수리 (2개 조합 각 1장씩)
        cardList.add(new RepairToolCard(Set.of(TargetToolType.PICKAX, TargetToolType.CART)));
        cardList.add(new RepairToolCard(Set.of(TargetToolType.PICKAX, TargetToolType.LIGHT)));
        cardList.add(new RepairToolCard(Set.of(TargetToolType.CART, TargetToolType.LIGHT)));

        // 낙석 3장
        for (int i = 0; i < 3; i++) cardList.add(new FallingRockCard());

        // 지도 6장
        for (int i = 0; i < 6; i++) cardList.add(new MapCard());

        Collections.shuffle(cardList);

        GameCardPool pool = new GameCardPool();
        pool.id = poolId;
        pool.cards = new LinkedList<>(cardList);
        return pool;
    }

    public void shuffleCards() {
        if (cards.isEmpty()) {
            throw new BusinessException(CardPoolErrorCode.NO_CARDS_EXIST);
        }
        List<Card> cardList = new ArrayList<>(cards);
        Collections.shuffle(cardList);
        cards = new LinkedList<>(cardList);
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
