package com.goldstone.saboteur_backend.service.game;

import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.game.GameCardPool;
import com.goldstone.saboteur_backend.domain.game.GameTurnManager;
import com.goldstone.saboteur_backend.domain.mapping.UserGameRoom;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.domain.user.UserCardDeck;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.UserErrorCode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GameService {
    private final GameTurnManager turnManager;
    private final Map<User, UserCardDeck> userCardDecks;
    private final GameCardPool cardPool;

    public GameService(List<UserGameRoom> userGameRooms, GameCardPool cardPool) {
        this.turnManager = new GameTurnManager(userGameRooms);
        this.cardPool = cardPool;
        this.userCardDecks = new HashMap<>();

        // 초기 카드 분배
        int cardsPerPlayer = getCardsPerPlayer(userGameRooms.size());
        for (UserGameRoom userGameRoom : userGameRooms) {
            User user = userGameRoom.getUser();
            List<Card> cards = new ArrayList<>();
            for (int i = 0; i < cardsPerPlayer; i++) {
                cards.add(cardPool.drawCard());
            }
            userCardDecks.put(user, new UserCardDeck(user, cards));
        }
    }

    private int getCardsPerPlayer(int playerCount) {
        if (playerCount >= 3 && playerCount <= 5) return 6;
        if (playerCount >= 6 && playerCount <= 7) return 5;
        if (playerCount >= 8 && playerCount <= 10) return 4;
        return 0;
    }

    // 카드 사용(또는 버림)
    public boolean playCard(User user, Card card) {
        UserCardDeck deck = userCardDecks.get(user);
        if (deck == null) throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
        return deck.useCard(card);
    }

    // 턴 종료 시 카드 드로우
    public User nextTurn() {
        User currentUser = turnManager.getCurrentTurnUser();
        UserCardDeck deck = userCardDecks.get(currentUser);

        // 턴 종료 직전에 카드 드로우 (덱이 남아있을 때)
        if (!cardPool.isEmpty()) {
            deck.addCard(cardPool.drawCard());
        }

        // 다음 플레이어로 이동
        return turnManager.nextTurn();
    }
}
