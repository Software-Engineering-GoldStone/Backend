package com.goldstone.saboteur_backend.domain.game;

import com.goldstone.saboteur_backend.domain.common.BaseEntity;
import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class GameTimer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double time;

    private Double maxTime;

    // start()
    // stop()
    // reset()
    // timeout()
}
