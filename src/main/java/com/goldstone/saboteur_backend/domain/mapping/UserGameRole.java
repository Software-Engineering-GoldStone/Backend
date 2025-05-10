package com.goldstone.saboteur_backend.domain.mapping;

import com.goldstone.saboteur_backend.domain.GameRoom;
import com.goldstone.saboteur_backend.domain.User;
import com.goldstone.saboteur_backend.domain.common.BaseEntity;
import com.goldstone.saboteur_backend.domain.enums.GameRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class UserGameRole extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_room_id")
    private GameRoom gameRoom;

    @ManyToOne
    @JoinColumn(name = "user_game_room_id")
    private UserGameRoom userGameRoom;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Integer round;

    @Enumerated(EnumType.STRING)
    private GameRole role;

    public UserGameRole(
            GameRoom gameRoom, UserGameRoom userGameRoom, User user, GameRole role, Integer round) {
        this.gameRoom = gameRoom;
        this.userGameRoom = userGameRoom;
        this.user = user;
        this.role = role;
        this.round = round;
    }
}
