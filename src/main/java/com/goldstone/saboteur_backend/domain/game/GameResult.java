package com.goldstone.saboteur_backend.domain.game;

import com.goldstone.saboteur_backend.domain.user.UserRank;
import com.goldstone.saboteur_backend.domain.common.BaseEntity;
import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class GameResult extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double totalPlayTime;

    @OneToOne
    @JoinColumn(name = "game_room_id")
    private GameRoom gameRoom;

    @OneToMany(mappedBy = "gameResult")
    private List<UserRank> userRank;

    // saveResult (repository 계층..?)

    // getFinalRanks() 는 getUserRank() 와 다른 메서드..?
}
