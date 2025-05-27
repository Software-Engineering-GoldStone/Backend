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
        Queue<Card> cards = new LinkedList<>();
        for (int i = 0; i < 44; i++) {
            cards.add(new PathCard(PathCardType.CROSSROAD, false));
        }
        for (int i = 0; i < 27; i++) {
            cards.add(new ActionCard());
        }
        cardPool = new GameCardPool(cards);
    }

    private int getCardsPerPlayer(int playerCount) {
        if (playerCount >= 3 && playerCount <= 5) return 6;
        if (playerCount >= 6 && playerCount <= 7) return 5;
        if (playerCount >= 8 && playerCount <= 10) return 4;
        return 0;
    }

    @ParameterizedTest(name = "플레이어 수별 역할 분배 테스트 (playerCount={0})")
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

    @ParameterizedTest(name = "플레이어 수별 턴 진행 테스트 (playerCount={0})")
    @ValueSource(ints = {3, 4, 5, 6, 7, 8, 9, 10})
    @DisplayName("플레이어 수에 따른 턴 관리 및 턴 진행 검증")
    void testTurnManagerByPlayerCount(int playerCount) {
        prepareTestData(playerCount);
        GameTurnManager turnManager = new GameTurnManager(userGameRooms);
        User firstPlayer = users.get(0);
        assertEquals(
                firstPlayer,
                turnManager.getCurrentTurnUser(),
                playerCount + "명일 때 첫 턴은 첫 번째 플레이어여야 함");
        for (int i = 0; i < users.size(); i++) {
            turnManager.nextTurn();
        }
        assertEquals(
                firstPlayer,
                turnManager.getCurrentTurnUser(),
                playerCount + "명일 때 한 바퀴 돌고 다시 첫 플레이어여야 함");
    }

    @ParameterizedTest(name = "플레이어 수별 라운드 관리 테스트 (playerCount={0})")
    @ValueSource(ints = {3, 4, 5, 6, 7, 8, 9, 10})
    @DisplayName("플레이어 수에 따른 라운드 관리 검증")
    void testRoundManagerByPlayerCount(int playerCount) {
        prepareTestData(playerCount);
        GameRoundManager roundManager = new GameRoundManager();
        roundManager.startRound();
        assertNotNull(roundManager.getRoundStartTime(), playerCount + "명일 때 라운드 시작 시간이 기록되어야 함");
        roundManager.endRound(GameRole.SABOTEUR);
        assertNotNull(roundManager.getRoundEndTime(), playerCount + "명일 때 라운드 종료 시간이 기록되어야 함");
        assertNotNull(roundManager.getWinnerRole(), playerCount + "명일 때 승자 역할이 결정되어야 함");
        assertEquals(
                GameRole.SABOTEUR,
                roundManager.getWinnerRole(),
                playerCount + "명일 때 금을 찾지 못하면 사보타지 승리");
    }
}
