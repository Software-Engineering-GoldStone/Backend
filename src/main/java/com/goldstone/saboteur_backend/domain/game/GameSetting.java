package com.goldstone.saboteur_backend.domain.game;

import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.domain.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class GameSetting extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "game_room_id")
    private GameRoom gameRoom;

    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    private String title;

    private Integer maxPlayers = 10;

    private Integer minPlayers = 3;

    public GameSetting(GameRoom gameRoom, String title, int maxPlayers, int minPlayers) {
        this.gameRoom = gameRoom;
        this.title = title;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
    }

    public void modifyMaxUsers(Integer count) {
        this.maxPlayers = count;
    }

    public void modifyMinUsers(Integer count) {
        this.minPlayers = count;
    }

    public void modifyTitle(String title) {
        this.title = title;
    }
}
