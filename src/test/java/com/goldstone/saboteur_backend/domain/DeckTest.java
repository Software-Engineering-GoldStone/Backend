package com.goldstone.saboteur_backend.domain;

import static org.junit.jupiter.api.Assertions.*;

import com.goldstone.saboteur_backend.domain.game.GameCardPool;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.domain.mapping.UserGameRoom;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.service.game.GameService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
        // 공식 룰에 맞는 카드풀 생성
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

        for (int i = 0; i < playerCount; i++) {
            User beforeTurnUser = users.get(i);
            int beforeDeckSize =
                    gameService.getUserCardDecks().get(beforeTurnUser).getCards().size();
            int beforePoolSize = cardPool.getCards().size();

            gameService.nextTurn();

            int afterDeckSize =
                    gameService.getUserCardDecks().get(beforeTurnUser).getCards().size();
            int afterPoolSize = cardPool.getCards().size();

            if (beforePoolSize > 0) {
                assertEquals(beforeDeckSize + 1, afterDeckSize);
                assertEquals(beforePoolSize - 1, afterPoolSize);
            } else {
                assertEquals(beforeDeckSize, afterDeckSize);
                assertEquals(0, afterPoolSize);
            }
        }
    }
}
