package com.goldstone.saboteur_backend;

import static org.junit.jupiter.api.Assertions.*;

import com.goldstone.saboteur_backend.domain.card.ActionCard;
import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.card.PathCard;
import com.goldstone.saboteur_backend.domain.enums.GameRole;
import com.goldstone.saboteur_backend.domain.enums.PathCardType;
import com.goldstone.saboteur_backend.domain.game.*;
import com.goldstone.saboteur_backend.domain.mapping.UserGameRole;
import com.goldstone.saboteur_backend.domain.mapping.UserGameRoom;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.domain.user.UserCardDeck;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("사보타지 보드게임 공식 룰 턴 및 역할 분배 테스트")
public class GameRoleAndTurnTest {

    private GameRoom gameRoom;
    private List<User> users;
    private List<UserGameRoom> userGameRooms;
    private GameCardPool cardPool;

    @BeforeEach
    void setUp() {
        // 게임방 생성 (플레이어 수는 테스트마다 다름)
        User master = new User("호스트", LocalDate.now());
        gameRoom = new GameRoom(master, "테스트 게임", 10, 3);
    }

    private void prepareTestData(int playerCount) {
        // 테스트용 유저 생성
        users = new ArrayList<>();
        for (int i = 1; i <= playerCount; i++) {
            users.add(new User("플레이어" + i, LocalDate.now()));
        }

        // UserGameRoom 생성
        userGameRooms = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            userGameRooms.add(new UserGameRoom(gameRoom, users.get(i)));
        }

        // 카드풀(덱) 생성 (공식 룰에 따라: 길(굴) 44장, 행동(액션) 27장)
        Queue<Card> cards = new LinkedList<>();
        // 길(굴) 카드 44장 추가
        for (int i = 0; i < 44; i++) {
            cards.add(new PathCard(PathCardType.CROSSROAD, false)); // 실제로는 다양한 타입의 PathCard를 추가해야 함
        }
        // 행동(액션) 카드 27장 추가
        for (int i = 0; i < 27; i++) {
            cards.add(new ActionCard()); // 실제로는 다양한 타입의 ActionCard를 추가해야 함
        }
        cardPool = new GameCardPool(cards);
    }

    private int getCardsPerPlayer(int playerCount) {
        if (playerCount >= 3 && playerCount <= 5) return 6;
        if (playerCount >= 6 && playerCount <= 7) return 5;
        if (playerCount >= 8 && playerCount <= 10) return 4;
        return 0;
    }

    /**
     * 플레이어 수(3~10명)별로 역할(사보타지/광부)이 공식 룰에 맞게 올바르게 분배되는지 검증합니다. - 플레이어 수에 따라 사보타지와 광부의 수가 정해져 있으므로,
     * 실제 분배 결과와 일치하는지 확인합니다. - 예: 7명일 때 사보타지 3명, 광부 4명이어야 함
     */
    @ParameterizedTest(name = "플레이어 수별 역할 분배 테스트 (playerCount={0})")
    /* ParameterizedTest 기능은 playerCount를  3, 4, 5, ..., 10 를 반복으로 실행해준다 */
    @ValueSource(ints = {3, 4, 5, 6, 7, 8, 9, 10})
    @DisplayName("플레이어 수에 따른 역할 분배 검증")
    void testRoleAssignmentByPlayerCount(int playerCount) {
        prepareTestData(playerCount);
        GameRoleAssignment roleAssignment = new GameRoleAssignment();
        List<UserGameRole> roles = roleAssignment.assignRoles(gameRoom, userGameRooms, 1);

        assertEquals(playerCount, roles.size(), playerCount + "명일 때 모든 플레이어에게 역할이 할당되어야 함");

        int saboteurCount = GameRoleAssignment.getRoleCardPair()[playerCount][0];
        int minerCount = GameRoleAssignment.getRoleCardPair()[playerCount][1];

        long actualSaboteurCount =
                roles.stream().filter(role -> role.getRole() == GameRole.SABOTEUR).count();
        long actualMinerCount =
                roles.stream().filter(role -> role.getRole() == GameRole.MINER).count();

        assertEquals(
                saboteurCount,
                actualSaboteurCount,
                playerCount + "명일 때 사보타지는 " + saboteurCount + "명이어야 함");
        assertEquals(
                minerCount, actualMinerCount, playerCount + "명일 때 광부는 " + minerCount + "명이어야 함");
    }

    /**
     * 플레이어 수(3~10명)별로 턴 관리가 올바르게 동작하는지 검증합니다. - 초기 턴은 반드시 첫 번째 플레이어여야 합니다. - 모든 플레이어가 한 번씩 턴을 진행한
     * 뒤, 다시 첫 번째 플레이어로 돌아와야 합니다. - 즉, 턴이 한 바퀴 돌고 다시 첫 플레이어로 돌아오는지 확인합니다.
     */
    @ParameterizedTest(name = "플레이어 수별 턴 진행 테스트 (playerCount={0})")
    @ValueSource(ints = {3, 4, 5, 6, 7, 8, 9, 10})
    @DisplayName("플레이어 수에 따른 턴 관리 및 턴 진행 검증")
    void testTurnManagerByPlayerCount(int playerCount) {
        prepareTestData(playerCount);
        GameTurnManager turnManager = new GameTurnManager(gameRoom, userGameRooms, cardPool);

        // 초기 턴은 첫 번째 플레이어
        User firstPlayer = users.get(0);
        assertEquals(firstPlayer, turnManager.getCurrentTurnUser(),
                playerCount + "명일 때 첫 턴은 첫 번째 플레이어여야 함");

        // nextTurn()을 한 번만 호출했을 때, 두 번째 플레이어가 할당되는지 확인
        turnManager.nextTurn();
        assertEquals(users.get(1), turnManager.getCurrentTurnUser(),
                playerCount + "명일 때 nextTurn() 한 번 호출 시 두 번째 플레이어여야 함");

        // 모든 플레이어 턴 진행 후 다시 첫 플레이어로
        for (int i = 0; i < users.size() - 1; i++) { // 이미 한 번 nextTurn()을 호출했으므로 users.size() - 2
            turnManager.nextTurn();
        }
        assertEquals(firstPlayer, turnManager.getCurrentTurnUser(),
                playerCount + "명일 때 한 바퀴 돌고 다시 첫 플레이어여야 함");
    }


    /**
     * 플레이어 수(3~10명)별로 카드 분배 및 턴 진행이 공식 룰에 맞게 동작하는지 검증합니다. - 각 플레이어는 플레이어 수에 따라 6/5/4장의 카드를 받아야 합니다.
     * - 카드 사용 후에는 카드 수가 1장 줄어야 합니다. - 턴이 넘어가기 직전에(턴 종료 시) 카드덱에서 1장을 뽑아 손에 추가합니다(덱이 남아있을 때). - 즉,
     * 카드덱이 남아있어 드로우 할 수 있다면, 턴이 끝날 때마다 손에 들고 있는 카드 수가 초기값과 같아야 합니다.
     */
    @ParameterizedTest(name = "플레이어 수별 카드 분배 및 턴 진행 테스트 (playerCount={0})")
    @ValueSource(ints = {3, 4, 5, 6, 7, 8, 9, 10})
    @DisplayName("플레이어 수에 따른 카드 분배 및 턴 진행 검증")
    void testCardDistributionAndTurnByOfficialRules(int playerCount) {
        prepareTestData(playerCount);
        GameTurnManager turnManager = new GameTurnManager(gameRoom, userGameRooms, cardPool);

        int expectedCardsPerPlayer = getCardsPerPlayer(playerCount);
        for (User user : users) {
            assertEquals(
                    expectedCardsPerPlayer,
                    turnManager.getUserCardDecks().get(user).getCards().size(),
                    playerCount + "명일 때 각 플레이어는 " + expectedCardsPerPlayer + "장의 카드를 받아야 함");
        }

        // 카드 사용 테스트
        User currentPlayer = turnManager.getCurrentTurnUser();
        UserCardDeck deck = turnManager.getUserCardDecks().get(currentPlayer);
        Card cardToPlay = deck.getCards().get(0);

        assertTrue(turnManager.playCard(cardToPlay), playerCount + "명일 때 카드 사용이 성공해야 함");
        assertEquals(
                expectedCardsPerPlayer - 1,
                deck.getCards().size(),
                playerCount + "명일 때 카드 사용 후 남은 카드는 " + (expectedCardsPerPlayer - 1) + "장이어야 함");

        // 턴 진행 후 카드 드로우 테스트 (덱이 남아있을 때)
        if (!cardPool.isEmpty()) {
            turnManager.nextTurn();
            User nextPlayer = turnManager.getCurrentTurnUser();
            assertEquals(
                    expectedCardsPerPlayer,
                    turnManager.getUserCardDecks().get(nextPlayer).getCards().size(),
                    playerCount
                            + "명일 때 턴 진행 후 카드를 한 장 드로우하여 "
                            + expectedCardsPerPlayer
                            + "장이 되어야 함");
        }
    }

    /**
     * 플레이어 수(3~10명)별로 라운드 관리가 올바르게 동작하는지 검증합니다. - 라운드 시작 시 시작 시간이 기록되어야 합니다. - 라운드 종료 시 종료 시간과 승자
     * 역할이 기록되어야 합니다. - 기본적으로 금을 찾지 못하면 사보타지가 승리합니다.
     */
    @ParameterizedTest(name = "플레이어 수별 라운드 관리 테스트 (playerCount={0})")
    @ValueSource(ints = {3, 4, 5, 6, 7, 8, 9, 10})
    @DisplayName("플레이어 수에 따른 라운드 관리 검증")
    void testRoundManagerByPlayerCount(int playerCount) {
        prepareTestData(playerCount);
        GameRoundManager roundManager = new GameRoundManager(gameRoom, userGameRooms, cardPool);

        roundManager.startRound();
        assertNotNull(roundManager.getRoundStartTime(), playerCount + "명일 때 라운드 시작 시간이 기록되어야 함");

        roundManager.endRound();
        assertNotNull(roundManager.getRoundEndTime(), playerCount + "명일 때 라운드 종료 시간이 기록되어야 함");
        assertNotNull(roundManager.getWinnerRole(), playerCount + "명일 때 승자 역할이 결정되어야 함");

        assertEquals(
                GameRole.SABOTEUR,
                roundManager.getWinnerRole(),
                playerCount + "명일 때 금을 찾지 못하면 사보타지 승리");
    }
}
