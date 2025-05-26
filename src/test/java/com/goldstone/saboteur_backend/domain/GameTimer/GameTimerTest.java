package com.goldstone.saboteur_backend.domain.GameTimer;

import static org.junit.jupiter.api.Assertions.*;

import com.goldstone.saboteur_backend.domain.game.GameTimer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("GameTimer Test")
public class GameTimerTest {

    private GameTimer gameTimer;
    private final double maxTime = 5.0;

    @BeforeEach
    public void setUp() {
        gameTimer = new GameTimer(maxTime);
    }

    @Test
    @DisplayName("GameTimer 시작 시 정상 실행 검증")
    public void testStart() {
        gameTimer.start();
        assertTrue(gameTimer.isRunning(), "타이머의 isRunning() 이 true 여야 함.");
    }

    @Test
    @DisplayName("타이머 정상 정지 검증")
    public void testStop() throws InterruptedException {
        gameTimer.start();
        Thread.sleep(1000); // 1초 동안 sleep
        gameTimer.stop();
        assertFalse(gameTimer.isRunning(), "stop() 이후 isRunning() 이 false 여야 함.");
        assertTrue(gameTimer.getTime() >= 1.0, "Elapsed time 이 1 초 이상이어야 함.");
    }

    @Test
    @DisplayName("타이머 정상 리셋 검증")
    public void testReset() {
        gameTimer.start();
        gameTimer.stop();
        gameTimer.reset();
        assertEquals(0.0, gameTimer.getTime(), "Time 이 0 으로 초기화 되어야 함.");
        assertFalse(gameTimer.isRunning(), "reset() 이후, isRunning() 이 false 여야 함.");
    }

    @Test
    @DisplayName("타이머 정상 작동 검증 - 세팅 시간 초과 시")
    public void testIsTimedOut() throws InterruptedException {
        gameTimer.start();
        Thread.sleep(6000); // 6초 동안 sleep
        assertTrue(gameTimer.istimedout(), "max time 경과 후 istimedout() 이 true 여야 함.");
    }

    @Test
    @DisplayName("타이머 정상 작동 검증 - 세팅 시간 미초과 시")
    public void testNotTimedOut() throws InterruptedException {
        gameTimer.start();
        Thread.sleep(3000); // 3초 동안 sleep
        gameTimer.stop();
        assertFalse(gameTimer.istimedout(), "max time 경과 전 istimedout() 이 false 여야 함.");
    }
}
