package com.goldstone.saboteur_backend.service.game;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.corundumstudio.socketio.SocketIOClient;
import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.game.GameCardPool;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.domain.mapping.UserGameRoom;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.domain.user.UserCardDeck;
import com.goldstone.saboteur_backend.session.GlobalSession;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GameServiceTest {
    @Autowired
    private GlobalSession globalSession;

    private GameRoom gameRoom;
    private List<UserGameRoom> userGameRooms;
    private GameCardPool cardPool;
    private GameService gameService;
    private List<User> users;
    private SocketIOClient mockClient;

    @BeforeEach
    void setup() {
        User host = new User("호스트", LocalDate.now());
        gameRoom = new GameRoom(host, "테스트 게임", 10, 3);

        userGameRooms = new ArrayList<>();
        users = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            User user = new User("플레이어" + i, LocalDate.now());
            userGameRooms.add(new UserGameRoom(gameRoom, user));
            users.add(user);
            globalSession.addUserSession(user);
        }

        cardPool = GameCardPool.createDefaultPool(gameRoom.getId());
        gameService = new GameService(userGameRooms, cardPool);

        mockClient = mock(SocketIOClient.class);
        when(mockClient.getSessionId()).thenReturn(UUID.randomUUID());
    }

    private Map<User, UserCardDeck> getUserCardDecks(GameService gameService) throws Exception {
        Field field = gameService.getClass().getDeclaredField("userCardDecks");
        field.setAccessible(true);
        return (Map<User, UserCardDeck>) field.get(gameService);
    }

    @Test
    @DisplayName("초기 카드 분배 검증: 플레이어당 6장, 카드풀 잔여 53장")
    void testInitialCardDistribution() throws Exception {
        Map<User, UserCardDeck> userCardDecks = getUserCardDecks(gameService);
        for (User user : users) {
            UserCardDeck deck = userCardDecks.get(user);
            assertNotNull(deck, "User의 cardDeck이 null이면 안 됨");
            assertFalse(deck.getCards().isEmpty(), "카드 덱이 비어있으면 안 됨");
            assertEquals(6, deck.getCards().size(), "플레이어당 6장의 카드가 분배되어야 함");
        }
        assertEquals(53, cardPool.getCards().size(), "카드풀에 남은 카드 수가 53장이어야 함");
    }

    @Test
    @DisplayName("카드 사용 이벤트 시뮬레이션: 플레이어가 카드를 사용하면 덱에서 제거됨")
    void testPlayCardViaWebSocket() throws Exception {
        Map<User, UserCardDeck> userCardDecks = getUserCardDecks(gameService);
        User player = users.get(0);
        UserCardDeck deck = userCardDecks.get(player);
        Card cardToPlay = deck.getCards().get(0);

        // 웹소켓 이벤트 핸들러에서 playCard 호출 시뮬레이션
        boolean result = gameService.playCard(player, cardToPlay);
        assertTrue(result, "카드 사용이 성공해야 함");
        assertFalse(deck.getCards().contains(cardToPlay), "카드가 덱에서 제거되어야 함");
    }

    @Test
    @DisplayName("턴 종료 이벤트 시뮬레이션: 턴 종료 시 카드 드로우")
    void testNextTurnViaWebSocket() throws Exception {
        Map<User, UserCardDeck> userCardDecks = getUserCardDecks(gameService);
        User firstPlayer = users.get(0);
        UserCardDeck deck = userCardDecks.get(firstPlayer);
        int initialDeckSize = deck.getCards().size();

        // 웹소켓 이벤트 핸들러에서 nextTurn 호출 시뮬레이션
        gameService.nextTurn();

        if (!cardPool.isEmpty()) {
            assertEquals(initialDeckSize + 1, deck.getCards().size(), "카드풀에 남은 카드가 있으면 덱에 1장 추가");
        } else {
            assertEquals(initialDeckSize, deck.getCards().size(), "카드풀이 비었으면 덱 크기 변동 없음");
        }
    }
}
