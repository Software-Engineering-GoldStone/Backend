package com.goldstone.saboteur_backend.domain;

import static org.junit.jupiter.api.Assertions.*;

import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.game.GameCardPool;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.domain.mapping.UserGameRoom;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.domain.user.UserCardDeck;
import com.goldstone.saboteur_backend.service.game.GameService;
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

    @BeforeEach
    void setUp() {
        User master = new User("호스트", LocalDate.now());
        gameRoom = new GameRoom(master, "테스트 게임", 10, 3);
    }

    private void prepareTestData(int playerCount) {
        users = new ArrayList<>();
        for (int i = 1; i <= playerCount; i++) {
            users.add(new User("플레이어" + i, LocalDate.now()));
        }
        userGameRooms = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            userGameRooms.add(new UserGameRoom(gameRoom, users.get(i)));
        }
        // 공식 룰에 맞는 카드풀 생성 (GameCardPool.createDefaultPool() 사용)
        cardPool = GameCardPool.createDefaultPool(gameRoom.getId());
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
    void testCardPoolAndDeckManagement(int playerCount) {
        prepareTestData(playerCount);
        GameService gameService = new GameService(userGameRooms, cardPool);

        // 초기 손패 분배 확인
        int initialHandSize = getCardsPerPlayer(playerCount);
        Map<User, UserCardDeck> userCardDecks = gameService.getUserCardDecks();
        for (User user : users) {
            assertEquals(initialHandSize, userCardDecks.get(user).getCards().size());
        }

        // 초기 카드풀 크기 확인 (공식 룰: 71장 - 플레이어 수 × 초기 손패)
        int expectedInitialPoolSize = 71 - (playerCount * initialHandSize);
        assertEquals(expectedInitialPoolSize, cardPool.getCards().size());

        // 실제 게임처럼 카드풀이 소진될 때까지 턴 진행 (플레이어 순환)
        int turn = 0;
        while (!cardPool.isEmpty()) {
            User currentUser = users.get(turn % playerCount);
            UserCardDeck deck = userCardDecks.get(currentUser);

            // 플레이어가 손패에서 1장을 내거나 버림 (임의로 첫 번째 카드 사용)
            int beforeDeckSize = deck.getCards().size();
            if (beforeDeckSize > 0) {
                Card cardToPlay = deck.getCards().get(0);
                assertTrue(gameService.playCard(currentUser, cardToPlay));
                // playCard()가 실제로 카드를 제거하는지 확인
                assertEquals(beforeDeckSize - 1, deck.getCards().size());
            }

            // 턴 종료 시 카드풀에서 1장 뽑기 (GameService의 nextTurn())
            int beforePoolSize = cardPool.getCards().size();
            gameService.nextTurn();
            int afterPoolSize = cardPool.getCards().size();

            // 카드풀에 카드가 남아있으면, 손패 크기는 1장 감소 후 1장 증가 = 유지
            if (beforePoolSize > 0) {
                // ※ 실제 게임 룰대로라면 손패 크기는 유지되어야 함
                assertEquals(beforeDeckSize, deck.getCards().size());
                assertEquals(beforePoolSize - 1, afterPoolSize);
            } else {
                // 카드풀이 비었으면, 손패는 1장 감소(뽑지 않음)
                assertEquals(beforeDeckSize - 1, deck.getCards().size());
                assertEquals(0, afterPoolSize);
            }
            turn++;
        }

        // 카드풀 소진 검증
        assertEquals(0, cardPool.getCards().size());
    }
}
