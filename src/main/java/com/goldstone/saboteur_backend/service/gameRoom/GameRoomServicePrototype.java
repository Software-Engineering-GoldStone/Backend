package com.goldstone.saboteur_backend.service.gameRoom;

import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.domain.game.GameSetting;
import com.goldstone.saboteur_backend.session.GlobalSession;
import jakarta.annotation.PostConstruct;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 프로토타입 전용 GameRoomService입니다.
 *
 * <p>이 서비스는 프로토타입에서만 사용되며, 프로토타입에 사용될 더미데이터를 생성합니다.
 *
 * <p>정식 버전에서는 제거될 예정입니다.
 */
@Component
@RequiredArgsConstructor
public class GameRoomServicePrototype {
    private final GlobalSession globalSession;

    @PostConstruct
    public void init() {
        GameRoom gameRoom = new GameRoom();
        GameSetting gameSetting = new GameSetting(gameRoom, null, "프로토타입 게임룸", 10, 3);

        // 프로토타입용 게임룸 설정
        gameRoom.setId(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"));
        gameRoom.setSetting(gameSetting);

        // 게임룸을 글로벌 세션에 등록합니다.
        globalSession.addGameRoomSession(gameRoom);

        System.out.println("[프로토타입용 게임 룸 초기화 완료]");
        System.out.println("[PROTOTYPE] GameRoom ID: " + gameRoom.getId());
    }
}
