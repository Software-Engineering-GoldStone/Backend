package com.goldstone.saboteur_backend.domain.GameCardPool;

import static org.assertj.core.api.Assertions.*;

import com.goldstone.saboteur_backend.domain.card.ActionCard.BreakToolCard;
import com.goldstone.saboteur_backend.domain.card.ActionCard.FallingRockCard;
import com.goldstone.saboteur_backend.domain.card.ActionCard.MapCard;
import com.goldstone.saboteur_backend.domain.card.ActionCard.RepairToolCard;
import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.card.PathCard;
import com.goldstone.saboteur_backend.domain.game.GameCardPool;
import java.util.*;
import java.util.stream.Collectors;

import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.CardPoolErrorCode;
import org.junit.jupiter.api.Test;

class GameCardPoolTest {

    @Test
    void createDefaultPool_생성된_카드_총합_확인() {
        GameCardPool pool = GameCardPool.createDefaultPool(UUID.randomUUID());
        List<Card> cards = new ArrayList<>(pool.getCards());

        // 전체 카드 수
        assertThat(cards.size()).isEqualTo(71);

        // 길 카드 수 (PathCard)
        long pathCardCount = cards.stream().filter(card -> card instanceof PathCard).count();
        assertThat(pathCardCount).isEqualTo(44);

        // 파괴 카드 수 (BreakToolCard)
        long breakCardCount = cards.stream().filter(card -> card instanceof BreakToolCard).count();
        assertThat(breakCardCount).isEqualTo(9);

        // 수리 카드 수 (RepairToolCard)
        long repairCardCount =
                cards.stream().filter(card -> card instanceof RepairToolCard).count();
        assertThat(repairCardCount).isEqualTo(9);

        // 낙석 카드 수 (FallingRockCard)
        long rockCardCount = cards.stream().filter(card -> card instanceof FallingRockCard).count();
        assertThat(rockCardCount).isEqualTo(3);

        // 지도 카드 수 (MapCard)
        long mapCardCount = cards.stream().filter(card -> card instanceof MapCard).count();
        assertThat(mapCardCount).isEqualTo(6);
    }

    @Test
    void createDefaultPool_생성된_카드풀은_각각_다른순서() {
        UUID poolId1 = UUID.randomUUID();
        UUID poolId2 = UUID.randomUUID();

        GameCardPool pool1 = GameCardPool.createDefaultPool(poolId1);
        GameCardPool pool2 = GameCardPool.createDefaultPool(poolId2);

        List<String> pool1CardTypes =
                pool1.getCards().stream()
                        .map(card -> card.getClass().getSimpleName())
                        .collect(Collectors.toList());

        List<String> pool2CardTypes =
                pool2.getCards().stream()
                        .map(card -> card.getClass().getSimpleName())
                        .collect(Collectors.toList());

        // 동일한 타입이라도 순서는 달라야 함
        assertThat(pool1CardTypes).isNotEqualTo(pool2CardTypes);
    }

    @Test
    void drawCard_모든_카드_소진_후_예외처리() {
        GameCardPool pool = GameCardPool.createDefaultPool(UUID.randomUUID());

        // 모든 카드 소진
        for (int i = 0; i < 71; i++) {
            pool.drawCard();
        }

        // 72번째 draw 시 예외 발생
        assertThatThrownBy(pool::drawCard)
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(CardPoolErrorCode.NO_CARDS_EXIST.getMessage());
    }


    @Test
    void shuffleCards_성공적으로_섞기() {
        GameCardPool pool = GameCardPool.createDefaultPool(UUID.randomUUID());
        List<Card> beforeShuffle = new ArrayList<>(pool.getCards());

        pool.shuffleCards();
        List<Card> afterShuffle = new ArrayList<>(pool.getCards());

        assertThat(beforeShuffle).isNotEqualTo(afterShuffle);
    }
}
