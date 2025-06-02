package com.goldstone.saboteur_backend.service.gameRoom;

import com.corundumstudio.socketio.SocketIOClient;
import com.goldstone.saboteur_backend.domain.board.Board;
import com.goldstone.saboteur_backend.domain.game.GameCardPool;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.domain.game.GameTurnManager;
import com.goldstone.saboteur_backend.domain.mapping.UserGameRoom;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.domain.user.UserCardDeck;
import com.goldstone.saboteur_backend.dtos.gameRoom.request.CreateGameRoomRequestDto;
import com.goldstone.saboteur_backend.dtos.gameRoom.request.JoinGameRoomRequestDto;
import com.goldstone.saboteur_backend.dtos.gameRoom.request.StartGameRequestDto;
import com.goldstone.saboteur_backend.exception.code.error.GameRoomErrorCode;
import com.goldstone.saboteur_backend.exception.code.error.UserErrorCode;
import com.goldstone.saboteur_backend.session.GlobalSession;
import com.goldstone.saboteur_backend.socketIo.SocketIoService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameRoomServiceImpl implements GameRoomService {

    @Autowired private final GlobalSession globalSession;
    private final SocketIoService socketIoService;

    @Override
    public GameRoom createGameRoom(CreateGameRoomRequestDto dto) throws Exception {
        User host = this.globalSession.getUserSession(dto.getUserId());
        if (host == null) {
            throw new Exception(UserErrorCode.USER_NOT_FOUND.getMessage());
        }

        GameRoom gameRoom = GameRoom.createGameRoomByHost(host);

        this.globalSession.addGameRoomSession(gameRoom);

        // 카드풀 생성 및 UUID 할당
        GameCardPool cardPool = GameCardPool.createDefaultPool(gameRoom.getId());
        this.globalSession.addGameCardPoolSession(gameRoom.getId(), cardPool);

        return gameRoom;
    }

    @Override
    public void joinGameRoom(SocketIOClient client, JoinGameRoomRequestDto dto) throws Exception {
        try {
            User user = this.globalSession.getUserSession(dto.getUserId());
            if (user == null) {
                throw new Exception(UserErrorCode.USER_NOT_FOUND.getMessage());
            }

            GameRoom gameRoom = this.globalSession.getGameRoomSession(dto.getGameRoomId());
            if (gameRoom == null) {
                throw new Exception(GameRoomErrorCode.GAME_ROOM_NOT_FOUND.getMessage());
            }
            if (!gameRoom.canJoinGameRoom()) {
                throw new Exception(GameRoomErrorCode.CANNOT_JOIN_MAX_PLAYER.getMessage());
            }

            gameRoom.addPlayer(user);

            client.joinRoom(gameRoom.getId().toString());
            client.sendEvent("gameRoomJoined");
        } catch (Exception e) {
            client.sendEvent("error", e.getMessage());
            throw e;
        }
    }

    public void startGame(StartGameRequestDto dto) throws Exception {
        GameRoom gameRoom = this.globalSession.getGameRoomSession(dto.getGameRoomId());
        if (gameRoom == null) {
            throw new Exception(GameRoomErrorCode.GAME_ROOM_NOT_FOUND.getMessage());
        }
        if (!gameRoom.canStartGame()) {
            throw new Exception(GameRoomErrorCode.CANNOT_START_GAME.getMessage());
        }

        gameRoom.startGame();
        this.globalSession.addGameBoardSession(gameRoom, new Board());

        GameTurnManager turnManager = new GameTurnManager(gameRoom.getUserGameRooms());
        this.globalSession.addTurnManagerSession(gameRoom.getId(), turnManager);

        // 카드 분배 로직
        GameCardPool cardPool = this.globalSession.getGameCardPoolSession(dto.getGameRoomId());
        List<UserGameRoom> userGameRooms = gameRoom.getUserGameRooms();

        // 카드 분배
        Map<User, UserCardDeck> userCardDecks =
                cardPool.assignCardsToUserDecks(
                        userGameRooms, GameCardPool.getCardsPerPlayer(userGameRooms.size()));

        for (User user : userCardDecks.keySet()) {
            user.setCardDeck(userCardDecks.get(user));
        }

        this.socketIoService.sendBroadCast(gameRoom.getId(), "gameStarted", "Hello, game started!");
    }
}
