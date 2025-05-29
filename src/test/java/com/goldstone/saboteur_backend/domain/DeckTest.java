package com.goldstone.saboteur_backend.domain;

import static org.junit.jupiter.api.Assertions.*;

import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.game.GameCardPool;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.domain.mapping.UserGameRoom;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.domain.user.UserCardDeck;
import com.goldstone.saboteur_backend.service.game.GameService;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("사보타지 카드풀/덱 관리 테스트")
public class DeckTest {
    private GameRoom gameRoom;
    private List<User> users;
    private List<UserGameRoom> userGameRooms;
    private GameCardPool cardPool;
    private GameService gameService;

    @BeforeEach
    void setUp() {
        User master = new User("호스트", LocalDate.now());
        gameRoom = new GameRoom(master, "테스트 게임", 10, 3);
    }

    private Map<User, UserCardDeck> getUserCardDecks(GameService gameService) throws Exception {
        Field field = gameService.getClass().getDeclaredField("userCardDecks");
        field.setAccessible(true);
        return (Map<User, UserCardDeck>) field.get(gameService);
    }

    private void prepareTestData(int playerCount) {
        users = new ArrayList<>();
        for (int i = 1; i <= playerCount; i++) {
            User user = new User("플레이어" + i, LocalDate.now());
            users.add(user);
        }
        userGameRooms = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            userGameRooms.add(new UserGameRoom(gameRoom, users.get(i)));
        }
        cardPool = GameCardPool.createDefaultPool(gameRoom.getId());
        gameService = new GameService(userGameRooms, cardPool);
    }

    private int getCardsPerPlayer(int playerCount) {
        if (playerCount >= 3 && playerCount <= 5) return 6;
        if (playerCount >= 6 && playerCount <= 7) return 5;
        if (playerCount >= 8 && playerCount <= 10) return 4;
        return 0;
    }

    @ParameterizedTest(name = "카드풀/덱 관리 테스트 (playerCount={0})")
    @ValueSource(ints = {3, 5, 10})
    @DisplayName("카드풀/덱 관리가 턴마다 정상 동작하는지 검증")
    void testCardPoolAndDeckManagement(int playerCount) throws Exception {
        prepareTestData(playerCount);
        Map<User, UserCardDeck> userCardDecks = getUserCardDecks(gameService);

        int initialHandSize = getCardsPerPlayer(playerCount);
        for (User user : users) {
            UserCardDeck deck = userCardDecks.get(user);
            assertEquals(initialHandSize, deck.getCards().size(), "초기 손패 분배 확인");
        }

        int expectedInitialPoolSize = 71 - (playerCount * initialHandSize);
        assertEquals(expectedInitialPoolSize, cardPool.getCards().size(), "카드풀 잔여량 확인");

        int turn = 0;
        while (!cardPool.isEmpty()) {
            User currentUser = users.get(turn % playerCount);
            UserCardDeck deck = userCardDecks.get(currentUser);

            int beforeDeckSize = deck.getCards().size();
            if (beforeDeckSize > 0) {
                Card cardToPlay = deck.getCards().get(0);
                assertTrue(gameService.playCard(currentUser, cardToPlay), "카드 사용 성공");
                assertEquals(beforeDeckSize - 1, deck.getCards().size(), "덱에서 카드 제거 확인");
            }

            int beforePoolSize = cardPool.getCards().size();
            gameService.nextTurn();
            int afterPoolSize = cardPool.getCards().size();

            if (beforePoolSize > 0) {
                assertEquals(beforeDeckSize, deck.getCards().size(), "턴 종료 시 덱 크기 유지");
                assertEquals(beforePoolSize - 1, afterPoolSize, "카드풀에서 1장 감소");
            } else {
                assertEquals(0, afterPoolSize, "카드풀이 비어있음");
            }
            turn++;
        }

        assertEquals(0, cardPool.getCards().size(), "카드풀 소진 확인");
    }
}
