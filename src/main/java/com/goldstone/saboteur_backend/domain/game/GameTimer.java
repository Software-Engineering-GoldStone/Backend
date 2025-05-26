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

    private Double time = 0.0;

    private Double maxTime;

    private boolean running = false;

    private long startTime;

    public GameTimer(Double maxTime) { //커스텀(나머지 변수의 초기값은 세팅 필요x)
        this.maxTime = maxTime;
    }

    public void start() {
        if (!running) {
            this.startTime = System.currentTimeMillis();
            this.running = true;
        }
    }

    public void stop() {
        if (running) {
            this.time += (System.currentTimeMillis() - this.startTime) / 1000.0;
            this.running = false;
        }
    }

    public void reset() {
        this.time = 0.0;
        this.running = false;
    }

    public boolean istimedout() {
        if (running) {
            double elapsedTime = (System.currentTimeMillis() - this.startTime) / 1000.0; //stop 안 된 상태에서
            return (this.time + elapsedTime) >= this.maxTime;
        }
        return this.time >= this.maxTime; //한 번 stop 된 상태에서
    }
}
