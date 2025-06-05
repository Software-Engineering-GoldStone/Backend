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
import com.goldstone.saboteur_backend.exception.BusinessException;
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
    public GameRoom createGameRoom(CreateGameRoomRequestDto dto) {
        User host = this.globalSession.getUserSession(dto.getUserId());
        if (host == null) {
            throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
        }

        GameRoom gameRoom = GameRoom.createGameRoomByHost(host);

        this.globalSession.addGameRoomSession(gameRoom);

        return gameRoom;
    }

    @Override
    public GameRoom joinGameRoom(SocketIOClient client, JoinGameRoomRequestDto dto) {
        User user = this.globalSession.getUserSession(dto.getUserId());
        if (user == null) {
            throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
        }

        GameRoom gameRoom = this.globalSession.getGameRoomSession(dto.getGameRoomId());
        if (gameRoom == null) {
            throw new BusinessException(GameRoomErrorCode.GAME_ROOM_NOT_FOUND);
        }

        if (gameRoom.getSetting().getHost() == null) {
            System.out.println("[GameRoomServiceImpl] User: " + gameRoom.getSetting().getHost());
            gameRoom.getSetting().setHost(user);
        }

        gameRoom.checkJoinGameRoom(user);

        gameRoom.addPlayer(user);
        client.joinRoom(gameRoom.getId().toString());

        return gameRoom;
    }

    public GameRoom startGame(StartGameRequestDto dto) {
        GameRoom gameRoom = this.globalSession.getGameRoomSession(dto.getGameRoomId());
        if (gameRoom == null) {
            throw new BusinessException(GameRoomErrorCode.GAME_ROOM_NOT_FOUND);
        }
        gameRoom.canStartGame(dto.getUserId());
        gameRoom.startGame();

        // 보드 생성
        this.globalSession.addGameBoardSession(gameRoom, new Board());

        // 턴 매니저 생성
        GameTurnManager turnManager = new GameTurnManager(gameRoom.getUserGameRooms());
        this.globalSession.addTurnManagerSession(gameRoom.getId(), turnManager);

        // 카드풀 생성 및 UUID 할당
        GameCardPool cardPool = GameCardPool.createDefaultPool(gameRoom.getId());
        this.globalSession.addGameCardPoolSession(gameRoom.getId(), cardPool);

        // 카드 분배
        List<UserGameRoom> userGameRooms = gameRoom.getUserGameRooms();
        int cardPerPlayer = GameCardPool.getCardsPerPlayer(userGameRooms.size());
        Map<User, UserCardDeck> userCardDecks =
                cardPool.assignCardsToUserDecks(userGameRooms, cardPerPlayer);

        for (User user : userCardDecks.keySet()) {
            user.setCardDeck(userCardDecks.get(user));
        }

        return gameRoom;
    }
}
