package com.goldstone.saboteur_backend.service.card.actionCard;

import static org.junit.jupiter.api.Assertions.*;

import com.goldstone.saboteur_backend.domain.card.actionCard.BreakToolCard;
import com.goldstone.saboteur_backend.domain.enums.ActionCardType;
import com.goldstone.saboteur_backend.domain.enums.CardType;
import com.goldstone.saboteur_backend.domain.enums.PlayerToolStatus;
import com.goldstone.saboteur_backend.domain.enums.TargetToolType;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.domain.user.UserCardDeck;
import com.goldstone.saboteur_backend.dtos.card.request.UseCardRequest;
import com.goldstone.saboteur_backend.dtos.card.response.UseCardResponse;
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
        BreakToolCard breakToolCard = new BreakToolCard(TargetToolType.PICKAX);
        breakToolCard.setCardId(cardId);

        // 카드덱 생성 후, 곡괭이 고장 카드 덱에 추가, user에게 덱 연결
        UserCardDeck userCardDeck = new UserCardDeck();
        userCardDeck.addCard(breakToolCard);
        user.setCardDeck(userCardDeck);

        // 세션에 유저들 추가
        session.addUserSession(user);
        session.addUserSession(targetUser);

        // 요청 객체 생성
        UseCardRequest request = new UseCardRequest();
        request.setUserId(user.getId());
        request.setCardId(breakToolCard.getCardId());
        request.setCardType(CardType.ACTION);
        request.setActionCardType(ActionCardType.DESTROY);
        request.setTargetUserId(targetUser.getId());

        assertTrue(user.getCardDeck().getCards().size() == 1);

        // ActionCard의 useActionCard() 호출하여 카드 사용
        UseCardResponse response = actionCardService.useActionCard(request);

        assertTrue(user.getCardDeck().getCards().size() == 0);

        assertEquals(targetUser.getId(), response.getAffectedUserId());
        assertEquals(TargetToolType.PICKAX, response.getAffectedTool());
        assertEquals(PlayerToolStatus.BROKEN, response.getNewStatus());
        assertTrue(response.getMessage().contains("도구") || response.getMessage().contains("고장"));
    }
}
