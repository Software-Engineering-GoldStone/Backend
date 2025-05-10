package com.goldstone.saboteur_backend.domain;

import com.goldstone.saboteur_backend.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class UserRank extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "`rank`")
    private Integer rank;

    private Integer goldCount;

    @ManyToOne
    @JoinColumn(name = "game_result_id")
    private GameResult gameResult;
}
