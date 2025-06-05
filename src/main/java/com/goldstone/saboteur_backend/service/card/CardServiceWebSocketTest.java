package com.goldstone.saboteur_backend.service.card;

import com.goldstone.saboteur_backend.domain.board.Board;
import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.card.PathCard;
import com.goldstone.saboteur_backend.domain.card.actionCard.BreakToolCard;
import com.goldstone.saboteur_backend.domain.card.actionCard.FallingRockCard;
import com.goldstone.saboteur_backend.domain.card.actionCard.MapCard;
import com.goldstone.saboteur_backend.domain.card.actionCard.RepairToolCard;
import com.goldstone.saboteur_backend.domain.enums.PathCardType;
import com.goldstone.saboteur_backend.domain.enums.TargetToolType;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.domain.user.UserCardDeck;
import com.goldstone.saboteur_backend.session.GlobalSession;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashSet;
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

        HashSet<TargetToolType> set = new HashSet<>();
        set.add(TargetToolType.PICKAX);
        RepairToolCard card1 = new RepairToolCard(set);
        card1.selectTool(TargetToolType.PICKAX);
        UUID cardId1 = card1.getCardId();

        MapCard card2 = new MapCard();
        UUID cardId2 = card2.getCardId();

        FallingRockCard card3 = new FallingRockCard();
        UUID cardId3 = card3.getCardId();

        // 카드 등록
        List<Card> cardList = new ArrayList<>();
        cardList.add(card);
        cardList.add(card1);
        cardList.add(card2);
        cardList.add(card3);

        UserCardDeck userDeck = new UserCardDeck(user, cardList);
        user.setCardDeck(userDeck);

        // 세션 등록
        globalSession.addUserSession(user);
        globalSession.addUserSession(targetUser);
        globalSession.addGameRoomSession(gameRoom);
        globalSession.addGameBoardSession(gameRoom, board);

        System.out.println(
                """
                        도구 고장 카드
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


        System.out.println(
                """
                        도구 수리 카드
                        {
                          "userId": "%s",
                          "cardId": "%s",
                          "cardType": "ACTION",
                          "actionCardType": "REPAIR",
                          "targetUserId": "%s",
                          "roomId": "%s"
                        }
                        """
                        .formatted(userId, cardId1, targetUserId, roomId, card1));
        System.out.println("repairableTools: " + card1.getRepairableTools());

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


        PathCard pathCard2 = new PathCard(PathCardType.CROSSROAD, false); // 예: 십자형 카드
        UUID pathCardId2 = pathCard2.getCardId();
        cardList.add(pathCard2); // 핸드에 추가

        System.out.println("[DEBUG] 길카드 ID: " + pathCardId2);
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
                        .formatted(userId, pathCardId2, roomId));


        System.out.println(
                """
                        {
                          "userId": "%s",
                          "cardId": "%s",
                          "cardType": "ACTION",
                          "roomId": "%s",
                          "targetCellX": 8,
                          "targetCellY": 2
                        }
                        """
                        .formatted(userId, cardId2, roomId));

        System.out.println(
                """
                        {
                          "userId": "%s",
                          "cardId": "%s",
                          "cardType": "ACTION",
                          "roomId": "%s",
                          "targetCellX": 2,
                          "targetCellY": 1
                        }
                        """
                        .formatted(userId, cardId3, roomId));


    }

}


