package com.goldstone.saboteur_backend.service.gameRoom;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import com.corundumstudio.socketio.SocketIOClient;
import com.goldstone.saboteur_backend.domain.enums.GameRoomStatus;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.dtos.gameRoom.request.CreateGameRoomRequestDto;
import com.goldstone.saboteur_backend.dtos.gameRoom.request.JoinGameRoomRequestDto;
import com.goldstone.saboteur_backend.dtos.gameRoom.request.StartGameRequestDto;
import com.goldstone.saboteur_backend.exception.code.error.GameRoomErrorCode;
import com.goldstone.saboteur_backend.session.GlobalSession;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GameRoomServiceTest {

    @Autowired private GlobalSession globalSession;
    @Autowired private GameRoomService gameRoomService;

    private final SocketIOClient mockClient = mock(SocketIOClient.class);

    private final User host = new User("방장님", LocalDate.now());

    @BeforeEach
    void setUp() {
        this.globalSession.addUserSession(this.host);
    }

    private List<User> createUsers(int count) {
        List<User> users = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            User user = new User("User" + i, LocalDate.now());
            users.add(user);
            this.globalSession.addUserSession(user);
        }

        return users;
    }

    @Test
    @DisplayName("게임 룸이 생성되고, 세션에서 가져온 게입 룸 아이디와 호스트 정보가 일치한다.")
    void createGameRoom() throws Exception {
        GameRoom gameRoom =
                this.gameRoomService.createGameRoom(new CreateGameRoomRequestDto(host.getId()));
        this.globalSession.addGameRoomSession(gameRoom);
        assertNotNull(gameRoom);

        GameRoom gameRoomFromSession = this.globalSession.getGameRoomSession(gameRoom.getId());
        assertEquals(gameRoom.getId(), gameRoomFromSession.getId());
        assertEquals(this.host.getId(), gameRoomFromSession.getSetting().getHost().getId());
    }

    @Test
    @DisplayName("새로운 유저가 게임 룸에 입장하고, 세션에서 가져온 게임 인원 수가 증가했음을 확인할 수 있어야 한다.")
    void joinGameRoom() throws Exception {
        User user = new User("User1", LocalDate.now());
        this.globalSession.addUserSession(user);

        GameRoom gameRoom =
                this.gameRoomService.createGameRoom(new CreateGameRoomRequestDto(host.getId()));
        this.gameRoomService.joinGameRoom(
                this.mockClient, new JoinGameRoomRequestDto(host.getId(), gameRoom.getId()));
        this.globalSession.addGameRoomSession(gameRoom);

        this.gameRoomService.joinGameRoom(
                this.mockClient, new JoinGameRoomRequestDto(user.getId(), gameRoom.getId()));

        GameRoom gameRoomFromSession = this.globalSession.getGameRoomSession(gameRoom.getId());
        assertEquals(2, gameRoomFromSession.getUserGameRooms().size());
    }

    @Test
    @DisplayName("게임 룸에 있는 유저 수가 10명을 초과한 상태에서 새로운 유저가 입장할 경우, 에러가 발생한다.")
    void joinGameRoomIfGameRoomPlayerOver10() throws Exception {
        List<User> users = this.createUsers(10);

        GameRoom gameRoom =
                this.gameRoomService.createGameRoom(new CreateGameRoomRequestDto(host.getId()));
        this.gameRoomService.joinGameRoom(
                this.mockClient, new JoinGameRoomRequestDto(host.getId(), gameRoom.getId()));
        this.globalSession.addGameRoomSession(gameRoom);

        // 9명의 유저까지 모두 입장 가능하다.
        for (int i = 0; i < 9; i++) {
            this.gameRoomService.joinGameRoom(
                    this.mockClient,
                    new JoinGameRoomRequestDto(users.get(i).getId(), gameRoom.getId()));
        }
        GameRoom gameRoomFromSession = this.globalSession.getGameRoomSession(gameRoom.getId());
        assertEquals(10, gameRoomFromSession.getUserGameRooms().size());

        // 10번째 유저가 입장했을 경우 인원 초과로 에러가 발생한다.
        Exception exception =
                assertThrows(
                        Exception.class,
                        () ->
                                this.gameRoomService.joinGameRoom(
                                        this.mockClient,
                                        new JoinGameRoomRequestDto(
                                                users.get(9).getId(), gameRoom.getId())));
        assertEquals(GameRoomErrorCode.CANNOT_JOIN_MAX_PLAYER.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName(
            "게임을 시작하는 조건이 충족되지 않았을 때 에러가 발생한다. 게임을 시작하는 주체는 호스트이고, 최소 인원 수를 만족했을 때만 게임 시작이 가능하다.")
    void startGameIfGameStartConditionsFulfilled() {
        GameRoom gameRoom =
                this.gameRoomService.createGameRoom(new CreateGameRoomRequestDto(host.getId()));
        this.gameRoomService.joinGameRoom(
                this.mockClient, new JoinGameRoomRequestDto(host.getId(), gameRoom.getId()));

        Exception exception;

        // 게임 시작을 위한 최소 인원 수를 맞추지 않았기 때문에 에러가 발생한다.
        exception =
                assertThrows(
                        Exception.class,
                        () ->
                                this.gameRoomService.startGame(
                                        new StartGameRequestDto(host.getId(), gameRoom.getId())));
        assertEquals(GameRoomErrorCode.CANNOT_START_GAME.getMessage(), exception.getMessage());

        // 이미 입장한 플레이어는 재입장이 불가능하다.
        exception =
                assertThrows(
                        Exception.class,
                        () ->
                                this.gameRoomService.joinGameRoom(
                                        this.mockClient,
                                        new JoinGameRoomRequestDto(
                                                host.getId(), gameRoom.getId())));
        assertEquals(
                GameRoomErrorCode.ALREADY_JOINED_GAME_ROOM.getMessage(), exception.getMessage());

        // 9명의 유저까지 모두 입장 가능하다.
        List<User> users = this.createUsers(4);
        for (int i = 0; i < 3; i++) {
            this.gameRoomService.joinGameRoom(
                    this.mockClient,
                    new JoinGameRoomRequestDto(users.get(i).getId(), gameRoom.getId()));
        }
        GameRoom gameRoomFromSession = this.globalSession.getGameRoomSession(gameRoom.getId());
        assertEquals(4, gameRoomFromSession.getUserGameRooms().size());

        // 최소 인원은 충족했지만, 호스트가 아닌 다른 플레이어는 게임을 시작할 수 없다.
        exception =
                assertThrows(
                        Exception.class,
                        () ->
                                this.gameRoomService.startGame(
                                        new StartGameRequestDto(
                                                users.get(0).getId(), gameRoom.getId())));
        assertEquals(
                GameRoomErrorCode.CANNOT_START_GAME_NOT_HOST.getMessage(), exception.getMessage());

        // 호스트가 게임을 시작할 수 있다.
        assertEquals(GameRoomStatus.READY, gameRoom.getStatus());
        this.gameRoomService.startGame(new StartGameRequestDto(host.getId(), gameRoom.getId()));
        assertEquals(GameRoomStatus.PLAYING, gameRoom.getStatus());

        // 게임이 시작된 후에는 더 이상 유저가 게임 룸에 입장할 수 없다.
        exception =
                assertThrows(
                        Exception.class,
                        () ->
                                this.gameRoomService.joinGameRoom(
                                        this.mockClient,
                                        new JoinGameRoomRequestDto(
                                                users.get(3).getId(), gameRoom.getId())));
        assertEquals(
                GameRoomErrorCode.CANNOT_JOIN_PLAYING_GAME_ROOM.getMessage(),
                exception.getMessage());
    }
}
