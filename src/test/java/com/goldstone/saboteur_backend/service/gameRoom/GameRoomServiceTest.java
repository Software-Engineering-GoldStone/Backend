package com.goldstone.saboteur_backend.service.gameRoom;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import com.corundumstudio.socketio.SocketIOClient;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.dtos.gameRoom.request.CreateGameRoomRequestDto;
import com.goldstone.saboteur_backend.dtos.gameRoom.request.JoinGameRoomRequestDto;
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
        this.globalSession.addGameRoomSession(gameRoom);

        this.gameRoomService.joinGameRoom(
                this.mockClient, new JoinGameRoomRequestDto(user.getId(), gameRoom.getId()));

        GameRoom gameRoomFromSession = this.globalSession.getGameRoomSession(gameRoom.getId());
        assertEquals(2, gameRoomFromSession.getUserGameRooms().size());
    }

    @Test
    @DisplayName("게임 룸에 있는 유저 수가 10명을 초과한 상태에서 새로운 유저가 입장할 경우, 에러가 발생한다.")
    void joinGameRoomIfGameRoomPlayerOver10() throws Exception {
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            User user = new User("User" + i, LocalDate.now());
            users.add(user);
            this.globalSession.addUserSession(user);
        }

        GameRoom gameRoom =
                this.gameRoomService.createGameRoom(new CreateGameRoomRequestDto(host.getId()));
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
        assertEquals(exception.getMessage(), GameRoomErrorCode.CANNOT_JOIN_MAX_PLAYER.getMessage());
    }
}
