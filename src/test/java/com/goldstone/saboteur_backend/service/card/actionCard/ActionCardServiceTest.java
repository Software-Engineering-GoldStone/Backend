package com.goldstone.saboteur_backend.service.card.actionCard;

import static org.junit.jupiter.api.Assertions.*;

import com.goldstone.saboteur_backend.domain.board.Board;
import com.goldstone.saboteur_backend.domain.board.Cell;
import com.goldstone.saboteur_backend.domain.card.GoalCard;
import com.goldstone.saboteur_backend.domain.card.actionCard.BreakToolCard;
import com.goldstone.saboteur_backend.domain.card.actionCard.MapCard;
import com.goldstone.saboteur_backend.domain.enums.*;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.domain.user.UserCardDeck;
import com.goldstone.saboteur_backend.dtos.card.request.UserTargetRequest;
import com.goldstone.saboteur_backend.dtos.card.response.UseCardResponse;
import com.goldstone.saboteur_backend.dtos.game.request.PlayCardRequestDto;
import com.goldstone.saboteur_backend.session.GlobalSession;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ActionCardServiceTest {

    @Autowired private ActionCardService actionCardService;

    @Autowired private GlobalSession session;

    @Test
    void useActionCard() {
        // 카드를 사용할 유저 객체 생성 & 보유 도구 상태 초기화
        User user = new User();
        user.setId(UUID.randomUUID());
        user.initToolStatus();

        // 카드를 사용 당할 유저 객체 생성 & 보유 도구 상태 초기화
        User targetUser = new User();
        targetUser.setId(UUID.randomUUID());
        targetUser.initToolStatus();

        // 곡괭이 고장 카드 생성
        UUID cardId = UUID.randomUUID();
        MapCard mapCard = new MapCard();
        mapCard.setCardId(cardId);
//        BreakToolCard breakToolCard = new BreakToolCard(TargetToolType.PICKAX);
//        breakToolCard.setCardId(cardId);

        // 카드덱 생성 후, 곡괭이 고장 카드 덱에 추가, user에게 덱 연결
        UserCardDeck userCardDeck = new UserCardDeck();
        userCardDeck.addCard(mapCard);
//        userCardDeck.addCard(breakToolCard);
        user.setCardDeck(userCardDeck);

        GameRoom gameRoom = new GameRoom();
        gameRoom.addPlayer(user);
        gameRoom.setId(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"));

        // 세션에 유저들 추가
        session.addUserSession(user);
        session.addUserSession(targetUser);
        session.addGameRoomSession(gameRoom);

        // 요청 객체 생성
//        UserTargetRequest request = new UserTargetRequest();
//        request.setUserId(user.getId());
//        request.setCardId(breakToolCard.getCardId());
//        request.setCardType(CardType.ACTION);
//        request.setActionCardType(ActionCardType.DESTROY);
//        request.setTargetUserId(targetUser.getId());

        Cell cell = new Cell(8, 2);
        cell.setCard(new GoalCard(GoalCardType.GOLD, PathCardType.CROSSROAD));
        Board board = new Board();
        session.addGameBoardSession(gameRoom, board);

        PlayCardRequestDto requestDto = new PlayCardRequestDto();
        requestDto.setUserId(user.getId());
        requestDto.setCardId(cardId);
        requestDto.setGameRoomId(gameRoom.getId());
        requestDto.setX(8);
        requestDto.setY(2);


        assertTrue(user.getCardDeck().getCards().size() == 1);

        // ActionCard의 useActionCard() 호출하여 카드 사용
        UseCardResponse response = actionCardService.useActionCard(gameRoom, mapCard, requestDto);

        //assertTrue(user.getCardDeck().getCards().size() == 0);

//        assertEquals(targetUser.getId(), response.getAffectedUserId());
//        assertEquals(TargetToolType.PICKAX, response.getAffectedTool());
//        assertEquals(PlayerToolStatus.BROKEN, response.getNewStatus());
//        assertTrue(response.getMessage().contains("도구") || response.getMessage().contains("고장"));

        assertTrue(response.getMessage().contains("선택한 목적지 카드는"));
        assertTrue(response.getMessage().contains("GOLD") || response.getMessage().contains("EMPTY"));
    }
}
