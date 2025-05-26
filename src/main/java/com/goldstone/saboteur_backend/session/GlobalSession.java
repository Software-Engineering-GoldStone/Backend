package com.goldstone.saboteur_backend.session;

import com.goldstone.saboteur_backend.domain.board.Board;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.domain.user.User;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;

@Component
public class GlobalSession {
    private final Map<UUID, User> userSession = new ConcurrentHashMap<>();
    private final Map<UUID, GameRoom> gameRoomSession = new ConcurrentHashMap<>();
    private final Map<UUID, Board> gameBoardSession = new ConcurrentHashMap<>();

    private <T> T wrapperCall(Supplier<T> action) {
        try {
            return action.get();
        } catch (Exception e) {
            System.err.println("Exception in void method: " + e.getMessage());
            return null;
        }
    }

    public boolean addUserSession(User user) {
        this.wrapperCall(() -> this.userSession.put(user.getId(), user));
        return true;
    }

    public User getUserSession(UUID userId) {
        return this.wrapperCall(() -> this.userSession.get(userId));
    }

    public boolean addGameRoomSession(GameRoom gameRoom) {
        this.wrapperCall(() -> this.gameRoomSession.put(gameRoom.getId(), gameRoom));
        return true;
    }

    public GameRoom getGameRoomSession(UUID gameRoomId) {
        return this.wrapperCall(() -> this.gameRoomSession.get(gameRoomId));
    }

    public boolean addGameBoardSession(GameRoom gameRoom, Board board) {
        this.wrapperCall(() -> this.gameBoardSession.put(gameRoom.getId(), board));
        return true;
    }

    public Board getGameBoardSession(UUID gameRoomId) {
        return this.wrapperCall(() -> this.gameBoardSession.get(gameRoomId));
    }
}
