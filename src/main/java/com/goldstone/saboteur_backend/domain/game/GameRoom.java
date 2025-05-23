package com.goldstone.saboteur_backend.domain.game;

import com.corundumstudio.socketio.SocketIOClient;
import com.goldstone.saboteur_backend.domain.common.BaseEntity;
import com.goldstone.saboteur_backend.domain.enums.GameRoomStatus;
import com.goldstone.saboteur_backend.domain.mapping.UserGameRole;
import com.goldstone.saboteur_backend.domain.mapping.UserGameRoom;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.dtos.gameRoom.request.CreateGameRoomRequestDto;
import com.goldstone.saboteur_backend.dtos.gameRoom.response.CreateGameRoomResponseDto;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class GameRoom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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
    private List<GameRoundLog> roundLogs;

    @OneToMany(mappedBy = "gameRoom")
    private List<UserGameRole> userGameRoles;

    @OneToMany(mappedBy = "gameRoom")
    private List<UserGameRoom> userGameRooms;

    public GameRoom(User master, String title, int maxPlayers, int minPlayers) {
        this.setting = new GameSetting(this, title, maxPlayers, minPlayers);
    }

    public static GameRoom createGameRoom(SocketIOClient client, CreateGameRoomRequestDto dto) {
        User master = new User(dto.getUserId().toString(), LocalDate.now());
        GameRoom gameRoom = new GameRoom(master, "겁나 쩌는 게임", 10, 3);
        CreateGameRoomResponseDto responseDto = CreateGameRoomResponseDto.from(gameRoom);
        client.joinRoom(gameRoom.getId().toString());
        client.sendEvent("gameRoomCreated", responseDto);
        return gameRoom;
    }

    public boolean canStartGame() {
        Integer playerCount = this.userGameRooms.size();
        Integer minPlayers = this.setting.getMinPlayers();
        Integer maxPlayers = this.setting.getMaxPlayers();
        return minPlayers <= playerCount && playerCount <= maxPlayers;
    }

    /** NOTE: Game이 종료되면 id + 1인 같은 속성을 가진 새로운 게임 객체를 생성하고, 그 객체에서 게임을 진행할 수 있도록 한다. */
    public void endGame() {
        this.status = GameRoomStatus.END;
    }

    void changeStatus(GameRoomStatus status) {
        this.status = status;
    }

    // 추가: 라운드 증가 메서드
    public void incrementRound() {
        this.round++;
    }

    public List<User> getPlayers() {
        return this.userGameRooms.stream().map(UserGameRoom::getUser).collect(Collectors.toList());
    }
}
