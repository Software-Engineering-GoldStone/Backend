package com.goldstone.saboteur_backend.domain.game;

import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.card.PathCard;
import com.goldstone.saboteur_backend.domain.card.actionCard.BreakToolCard;
import com.goldstone.saboteur_backend.domain.card.actionCard.FallingRockCard;
import com.goldstone.saboteur_backend.domain.card.actionCard.MapCard;
import com.goldstone.saboteur_backend.domain.card.actionCard.RepairToolCard;
import com.goldstone.saboteur_backend.domain.enums.PathCardType;
import com.goldstone.saboteur_backend.domain.enums.TargetToolType;
import com.goldstone.saboteur_backend.domain.mapping.UserGameRoom;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.domain.user.UserCardDeck;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.CardPoolErrorCode;
import java.util.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class GameCardPool {
    private UUID GameRoomId;
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
    public static GameCardPool createDefaultPool(UUID GameRoomId) {
        List<Card> cardList = new LinkedList<>();

        // DeadEnd 타입 (각 1장씩)
        cardList.add(new PathCard(PathCardType.BOTH_HORIZONTAL_DEADEND, false));
        cardList.add(new PathCard(PathCardType.BOTH_VERTICAL_DEADEND, false));
        cardList.add(new PathCard(PathCardType.CROSSROAD_DEADEND, false));
        cardList.add(new PathCard(PathCardType.HORIZONTAL_T_DEADEND, false));
        cardList.add(new PathCard(PathCardType.VERTICAL_T_DEADEND, false));
        cardList.add(new PathCard(PathCardType.LEFT_TURN_DEADEND, false));
        cardList.add(new PathCard(PathCardType.RIGHT_TURN_DEADEND, false));
        cardList.add(new PathCard(PathCardType.SINGLE_HORIZONTAL_DEADEND, false));
        cardList.add(new PathCard(PathCardType.SINGLE_VERTICAL_DEADEND, false));

        // 일반 타입
        for (int i = 0; i < 5; i++) cardList.add(new PathCard(PathCardType.CROSSROAD, false));
        for (int i = 0; i < 3; i++) cardList.add(new PathCard(PathCardType.HORIZONTAL, false));
        for (int i = 0; i < 5; i++) cardList.add(new PathCard(PathCardType.HORIZONTAL_T, false));
        for (int i = 0; i < 4; i++) cardList.add(new PathCard(PathCardType.LEFT_TURN, false));
        for (int i = 0; i < 4; i++) cardList.add(new PathCard(PathCardType.RIGHT_TURN, false));
        for (int i = 0; i < 4; i++) cardList.add(new PathCard(PathCardType.VERTICAL, false));
        for (int i = 0; i < 5; i++) cardList.add(new PathCard(PathCardType.VERTICAL_T, false));

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
        // 총 66장 검증
        if (cardList.size() != 66) {
            throw new IllegalStateException("카드풀이 71장으로 초기화되지 않았습니다.");
        }

        GameCardPool pool = new GameCardPool();
        pool.GameRoomId = GameRoomId;
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

    public Map<User, UserCardDeck> assignCardsToUserDecks(
            List<UserGameRoom> userGameRooms, int cardsPerPlayer) {
        Map<User, UserCardDeck> userCardDecks = new HashMap<>();
        for (UserGameRoom userGameRoom : userGameRooms) {
            User user = userGameRoom.getUser();
            List<Card> cards = new ArrayList<>();
            for (int i = 0; i < cardsPerPlayer; i++) {
                cards.add(this.drawCard());
            }
            userCardDecks.put(user, new UserCardDeck(user, cards));
        }
        return userCardDecks;
    }

    public static int getCardsPerPlayer(int playerCount) {
        if (playerCount >= 3 && playerCount <= 5) return 6;
        if (playerCount >= 6 && playerCount <= 7) return 5;
        if (playerCount >= 8 && playerCount <= 10) return 4;
        return 0;
    }
}
