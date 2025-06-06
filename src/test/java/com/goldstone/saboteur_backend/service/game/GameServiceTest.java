package com.goldstone.saboteur_backend.service.game;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.corundumstudio.socketio.SocketIOClient;
import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.card.GoldCard;
import com.goldstone.saboteur_backend.domain.enums.GameRole;
import com.goldstone.saboteur_backend.domain.game.GameCardPool;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.domain.game.GoldCardDeck;
import com.goldstone.saboteur_backend.domain.mapping.UserGameRole;
import com.goldstone.saboteur_backend.domain.mapping.UserGameRoom;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.domain.user.UserCardDeck;
import com.goldstone.saboteur_backend.session.GlobalSession;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GameServiceTest {
    @Autowired private GlobalSession globalSession;

    private GameRoom gameRoom;
    private List<UserGameRoom> userGameRooms;
    private GameCardPool cardPool;
    private List<User> users;
    private SocketIOClient mockClient;

    @BeforeEach
    void setup() {
        User host = new User("호스트", LocalDate.now());
        gameRoom = GameRoom.createGameRoomByHost(host);

        userGameRooms = new ArrayList<>();
        users = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            User user = new User("플레이어" + i, LocalDate.now());
            userGameRooms.add(new UserGameRoom(gameRoom, user));
            users.add(user);
            globalSession.addUserSession(user);
        }

        cardPool = GameCardPool.createDefaultPool(gameRoom.getId());
        globalSession.addGameRoomSession(gameRoom);
        globalSession.addGameCardPoolSession(gameRoom.getId(), cardPool);

        // 금덩이덱 추가
        GoldCardDeck goldDeck = new GoldCardDeck();
        globalSession.addGoldDeckSession(gameRoom.getId(), goldDeck);

        // 역할 분배
        List<UserGameRole> roles = new ArrayList<>();
        roles.add(new UserGameRole(gameRoom, userGameRooms.get(0), users.get(0), GameRole.MINER));
        roles.add(new UserGameRole(gameRoom, userGameRooms.get(1), users.get(1), GameRole.MINER));
        roles.add(
                new UserGameRole(gameRoom, userGameRooms.get(2), users.get(2), GameRole.SABOTEUR));
        globalSession.addRoleAssignment(gameRoom.getId(), roles);

        // 카드 분배
        int cardsPerPlayer = GameCardPool.getCardsPerPlayer(userGameRooms.size());
        for (UserGameRoom ugr : userGameRooms) {
            List<Card> cards = new ArrayList<>();
            for (int j = 0; j < cardsPerPlayer; j++) {
                cards.add(cardPool.drawCard());
            }
            UserCardDeck deck = new UserCardDeck(ugr.getUser(), cards);
            ugr.getUser().setCardDeck(deck);
        }

        mockClient = mock(SocketIOClient.class);
        when(mockClient.getSessionId()).thenReturn(UUID.randomUUID());
    }

    @Test
    @DisplayName("초기 카드 분배 검증: 플레이어당 6장, 카드풀 잔여 48장")
    void testInitialCardDistribution() {
        for (User user : users) {
            UserCardDeck deck = user.getCardDeck();
            assertNotNull(deck, "User의 cardDeck이 null이면 안 됨");
            assertFalse(deck.getCards().isEmpty(), "카드 덱이 비어있으면 안 됨");
            assertEquals(6, deck.getCards().size(), "플레이어당 6장의 카드가 분배되어야 함");
        }
        assertEquals(48, cardPool.getCards().size(), "카드풀에 남은 카드 수가 48장이어야 함");
    }

    @Test
    @DisplayName("카드 사용 시 덱에서 제거됨")
    void testPlayCard() {
        User player = users.get(0);
        UserCardDeck deck = player.getCardDeck();
        Card cardToPlay = deck.getCards().get(0);
        boolean result = deck.useCard(cardToPlay);
        assertTrue(result, "카드 사용이 성공해야 함");
        assertFalse(deck.getCards().contains(cardToPlay), "카드가 덱에서 제거되어야 함");
    }

    @Test
    @DisplayName("턴 종료 시 카드 드로우")
    void testNextTurn() {
        User firstPlayer = users.get(0);
        UserCardDeck deck = firstPlayer.getCardDeck();
        int initialDeckSize = deck.getCards().size();

        if (!cardPool.isEmpty()) {
            deck.addCard(cardPool.drawCard());
            assertEquals(initialDeckSize + 1, deck.getCards().size(), "카드풀에 남은 카드가 있으면 덱에 1장 추가");
        } else {
            assertEquals(initialDeckSize, deck.getCards().size(), "카드풀이 비었으면 덱 크기 변동 없음");
        }
    }

    @Test
    @DisplayName("광부 승리 시 금덩이 분배 및 점수 누적")
    void testMinerVictoryGoldDistribution() {
        GoldCardDeck goldDeck = globalSession.getGoldDeckSession(gameRoom.getId());
        List<UserGameRole> roles = globalSession.getRoleAssignment(gameRoom.getId());

        // 광부 인원수만큼 금덩이 카드 분배
        List<User> miners = new ArrayList<>();
        for (UserGameRole role : roles) {
            if (role.getRole() == GameRole.MINER) {
                miners.add(role.getUser());
            }
        }
        List<GoldCard> golds = goldDeck.drawGoldCards(miners.size());
        for (int i = 0; i < miners.size() && i < golds.size(); i++) {
            miners.get(i).addGoldCard(golds.get(i));
        }

        // 각 광부의 goldScore가 올바르게 누적되었는지 확인
        for (User miner : miners) {
            assertEquals(1, miner.getGoldCards().size(), "광부는 1장의 금덩이 카드를 받아야 함");
            assertEquals(
                    miner.getGoldCards().get(0).getAmount().intValue(),
                    miner.getGoldScore(),
                    "goldScore가 금덩이 카드의 amount와 같아야 함");
        }

        // 사보타지는 금덩이를 받지 않아야 함
        for (UserGameRole role : roles) {
            if (role.getRole() == GameRole.SABOTEUR) {
                assertEquals(
                        0, role.getUser().getGoldCards().size(), "사보타지는 광부 승리 시 금덩이를 받지 않아야 함");
                assertEquals(0, role.getUser().getGoldScore(), "사보타지의 goldScore는 0이어야 함");
            }
        }
    }

    @Test
    @DisplayName("사보타지 승리 시 금덩이 분배 및 점수 누적")
    void testSaboteurVictoryGoldDistribution() {
        GoldCardDeck goldDeck = globalSession.getGoldDeckSession(gameRoom.getId());
        List<UserGameRole> roles = globalSession.getRoleAssignment(gameRoom.getId());

        // 사보타지 승리 시: 사보타지 수에 관계없이 3장 분배 (사보타지 공식 룰)
        List<User> saboteurs = new ArrayList<>();
        for (UserGameRole role : roles) {
            if (role.getRole() == GameRole.SABOTEUR) {
                saboteurs.add(role.getUser());
            }
        }

        // 사보타지 승리 시 3장 뽑아서 사보타지들이 나눠가짐
        int goldCardsToDistribute = 3;
        List<GoldCard> golds = goldDeck.drawGoldCards(goldCardsToDistribute);

        // 사보타지 인원수만큼 순차적으로 분배
        for (int i = 0; i < golds.size(); i++) {
            User saboteur = saboteurs.get(i % saboteurs.size());
            saboteur.addGoldCard(golds.get(i));
        }

        // 사보타지가 금덩이를 받았는지 확인
        int totalSaboteurGoldCards = 0;
        int totalSaboteurScore = 0;
        for (User saboteur : saboteurs) {
            totalSaboteurGoldCards += saboteur.getGoldCards().size();
            totalSaboteurScore += saboteur.getGoldScore();
        }
        assertEquals(3, totalSaboteurGoldCards, "사보타지 승리 시 총 3장의 금덩이 카드가 분배되어야 함");
        assertTrue(totalSaboteurScore > 0, "사보타지의 총 goldScore가 0보다 커야 함");

        // 광부는 금덩이를 받지 않아야 함
        for (UserGameRole role : roles) {
            if (role.getRole() == GameRole.MINER) {
                assertEquals(
                        0, role.getUser().getGoldCards().size(), "광부는 사보타지 승리 시 금덩이를 받지 않아야 함");
                assertEquals(0, role.getUser().getGoldScore(), "광부의 goldScore는 0이어야 함");
            }
        }
    }

    @Test
    @DisplayName("카드 소진 시 사보타지 승리 조건 확인")
    void testSaboteurVictoryCondition() {
        // 모든 카드를 소진시키기
        while (!cardPool.isEmpty()) {
            cardPool.drawCard();
        }

        // 모든 플레이어의 덱을 비우기
        for (User user : users) {
            UserCardDeck deck = user.getCardDeck();
            deck.getCards().clear();
        }

        // 게임 종료 조건 확인 (카드 소진 = 사보타지 승리)
        boolean gameEnded = cardPool.isEmpty();
        for (User user : users) {
            if (!user.getCardDeck().getCards().isEmpty()) {
                gameEnded = false;
                break;
            }
        }
        assertTrue(gameEnded, "카드가 모두 소진되면 게임이 종료되어야 함 (사보타지 승리)");
    }

    @Test
    @DisplayName("게임 진행 중 각 플레이어의 goldScore 확인 가능")
    void testGoldScoreAccessDuringGame() {
        // 임의로 일부 유저에게 금덩이 카드 추가
        GoldCardDeck goldDeck = globalSession.getGoldDeckSession(gameRoom.getId());
        User testUser = users.get(0);

        // 게임 진행 중 금덩이 카드 획득 시뮬레이션
        GoldCard testGoldCard = goldDeck.drawGoldCards(1).get(0);
        testUser.addGoldCard(testGoldCard);

        // 게임 진행 중에도 goldScore 확인 가능
        assertEquals(
                testGoldCard.getAmount().intValue(),
                testUser.getGoldScore(),
                "게임 진행 중에도 유저의 goldScore를 확인할 수 있어야 함");
        assertEquals(1, testUser.getGoldCards().size(), "게임 진행 중에도 유저가 보유한 금덩이 카드 수를 확인할 수 있어야 함");
    }
}
