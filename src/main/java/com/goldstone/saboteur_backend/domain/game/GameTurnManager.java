package com.goldstone.saboteur_backend.domain.game;

import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.mapping.UserGameRoom;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.domain.user.UserCardDeck;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

@Getter
public class GameTurnManager {
    private GameRoom gameRoom;
    private List<UserGameRoom> userGameRooms;
    private GameCardPool cardPool;
    private Map<User, UserCardDeck> userCardDecks;
    private int currentTurnIndex = 0;

    public GameTurnManager(GameRoom gameRoom, List<UserGameRoom> userGameRooms, GameCardPool cardPool) {
        this.gameRoom = gameRoom;
        this.userGameRooms = userGameRooms;
        this.cardPool = cardPool;
        this.userCardDecks = new HashMap<>();

        int cardsPerPlayer = getCardsPerPlayer(userGameRooms.size());
        for (UserGameRoom ugr : userGameRooms) {
            User user = ugr.getUser();
            List<Card> cards = new ArrayList<>();
            for (int i = 0; i < cardsPerPlayer; i++) {
                cards.add(cardPool.drawCard());
            }
            // ※ UserCardDeck에 카드풀을 주입하지 않음 (외부에서 카드 드로우)
            userCardDecks.put(user, new UserCardDeck(user, cards, null));
        }
    }

    private int getCardsPerPlayer(int playerCount) {
        if (playerCount >= 3 && playerCount <= 5) return 6;
        if (playerCount >= 6 && playerCount <= 7) return 5;
        if (playerCount >= 8 && playerCount <= 10) return 4;
        return 0;
    }

    public User getCurrentTurnUser() {
        return userGameRooms.get(currentTurnIndex).getUser();
    }

    // 카드 사용(또는 버림)
    public boolean playCard(Card card) {
        User currentUser = getCurrentTurnUser();
        UserCardDeck deck = userCardDecks.get(currentUser);
        return deck.useCard(card);
    }

    // 턴 종료 직전에 카드 드로우
    public User nextTurn() {
        User currentUser = getCurrentTurnUser();
        UserCardDeck deck = userCardDecks.get(currentUser);

        // ※ UserCardDeck에 카드풀을 주입하지 않았으므로, GameTurnManager가 카드풀에서 뽑아 추가
        if (!cardPool.isEmpty()) {
            deck.addCard(cardPool.drawCard());
        }

        // 다음 플레이어로 이동
        currentTurnIndex = (currentTurnIndex + 1) % userGameRooms.size();
        return getCurrentTurnUser();
    }
}
