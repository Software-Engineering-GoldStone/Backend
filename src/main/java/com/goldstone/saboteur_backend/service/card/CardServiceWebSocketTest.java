package com.goldstone.saboteur_backend.service.card;

import com.goldstone.saboteur_backend.domain.board.Board;
import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.card.PathCard;
import com.goldstone.saboteur_backend.domain.card.actionCard.BreakToolCard;
import com.goldstone.saboteur_backend.domain.enums.PathCardType;
import com.goldstone.saboteur_backend.domain.enums.TargetToolType;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.domain.user.UserCardDeck;
import com.goldstone.saboteur_backend.session.GlobalSession;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardServiceWebSocketTest {

    private final GlobalSession globalSession;

    @PostConstruct
    public void init() {
        // 고정 UUID
        UUID userId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        UUID targetUserId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        UUID roomId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        // 유저 생성
        User user = new User();
        user.setId(userId);
        user.setNickname("테스터");
        user.initToolStatus();

        User targetUser = new User();
        targetUser.setId(targetUserId);
        targetUser.setNickname("타겟");
        targetUser.initToolStatus();

        // 게임룸 및 보드
        GameRoom gameRoom = new GameRoom();
        gameRoom.setId(roomId);
        Board board = new Board();

        // 랜덤 UUID를 가진 카드 생성
        BreakToolCard card = new BreakToolCard(TargetToolType.PICKAX);
        UUID cardId = card.getCardId();

        // 카드 등록
        List<Card> cardList = new ArrayList<>();
        cardList.add(card);
        UserCardDeck userDeck = new UserCardDeck(user, cardList);
        user.setCardDeck(userDeck);

        // 세션 등록
        globalSession.addUserSession(user);
        globalSession.addUserSession(targetUser);
        globalSession.addGameRoomSession(gameRoom);
        globalSession.addGameBoardSession(gameRoom, board);

        // 콘솔 출력
        System.out.println("[테스트 환경 초기화 완료]");
        System.out.println("[DEBUG] 카드 ID: " + cardId);
        System.out.println("[DEBUG] 유저 ID: " + userId);
        System.out.println("[DEBUG] 타겟 유저 ID: " + targetUserId);
        System.out.println("[DEBUG] 게임룸 ID: " + roomId);
        System.out.println();
        System.out.println("Postman WebSocket 요청 예시:");
        System.out.println(
                """
                {
                  "userId": "%s",
                  "cardId": "%s",
                  "cardType": "ACTION",
                  "actionCardType": "DESTROY",
                  "targetUserId": "%s",
                  "roomId": "%s"
                }
                """
                        .formatted(userId, cardId, targetUserId, roomId));

        // 길카드 테스트
        PathCard pathCard = new PathCard(PathCardType.CROSSROAD, false); // 예: 십자형 카드
        UUID pathCardId = pathCard.getCardId();
        cardList.add(pathCard); // 핸드에 추가

        System.out.println("[DEBUG] 길카드 ID: " + pathCardId);
        System.out.println();
        System.out.println("Postman WebSocket 길카드 설치 요청 예시:");
        System.out.println(
                """
                {
                  "userId": "%s",
                  "cardId": "%s",
                  "cardType": "PATH",
                  "roomId": "%s",
                  "targetCellX": 2,
                  "targetCellY": 1
                }
                """
                        .formatted(userId, pathCardId, roomId));
    }
}
