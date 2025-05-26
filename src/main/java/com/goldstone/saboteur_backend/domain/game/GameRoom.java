package com.goldstone.saboteur_backend.domain.game;

import com.goldstone.saboteur_backend.domain.common.BaseEntity;
import com.goldstone.saboteur_backend.domain.enums.GameRoomStatus;
import com.goldstone.saboteur_backend.domain.mapping.UserGameRole;
import com.goldstone.saboteur_backend.domain.mapping.UserGameRoom;
import com.goldstone.saboteur_backend.domain.user.User;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.*;

@ToString
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class GameRoom extends BaseEntity {
    @Id private UUID id;

    @Enumerated(EnumType.STRING)
    private GameRoomStatus status = GameRoomStatus.READY;

    private Integer round = 1;

    @OneToOne(mappedBy = "gameRoom")
    private GameSetting setting;

    @OneToOne(mappedBy = "gameRoom")
    private GameResult result;

    @OneToOne(mappedBy = "gameRoom")
    private GameLog gameLog;

    @OneToMany(mappedBy = "gameRoom")
    private List<GameRoundLog> roundLogs = new ArrayList<>();

    @OneToMany(mappedBy = "gameRoom")
    private List<UserGameRole> userGameRoles = new ArrayList<>();

    @OneToMany(mappedBy = "gameRoom")
    private List<UserGameRoom> userGameRooms = new ArrayList<>();

    public GameRoom(User host, String title, int maxPlayers, int minPlayers) {
        this.id = UUID.randomUUID();
        this.setting = new GameSetting(this, host, title, maxPlayers, minPlayers);
    }

    public static GameRoom createGameRoomByHost(User host) {
        GameRoom gameRoom = new GameRoom(host, "겁나 쩌는 게임", 10, 3);
        UserGameRoom userGameRoom = new UserGameRoom(gameRoom, host);

        gameRoom.userGameRooms.add(userGameRoom);
        return gameRoom;
    }

    public boolean canStartGame() {
        Integer playerCount = this.userGameRooms.size();
        Integer minPlayers = this.setting.getMinPlayers();
        Integer maxPlayers = this.setting.getMaxPlayers();

        return minPlayers <= playerCount && playerCount <= maxPlayers;
    }

    public boolean canJoinGameRoom() {
        return this.getUserGameRooms().size() + 1 > this.getSetting().getMaxPlayers();
    }

    public void startGame() {
        this.status = GameRoomStatus.PLAYING;
    }

    /** NOTE: Game이 종료되면 id + 1인 같은 속성을 가진 새로운 게임 객체를 생성하고, 그 객체에서 게임을 진행할 수 있도록 한다. */
    public void endGame() {
        this.status = GameRoomStatus.END;
        // 필요에 따라 추가 로직 필요.
    }

    private void changeStatus(GameRoomStatus status) {
        this.status = status;
    }

    public List<User> getPlayers() {
        return this.userGameRooms.stream().map(UserGameRoom::getUser).collect(Collectors.toList());
    }

    public void addPlayer(User user) {
        this.userGameRooms.add(new UserGameRoom(this, user));
    }
}
