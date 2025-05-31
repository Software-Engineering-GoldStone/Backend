package com.goldstone.saboteur_backend.service.card.actionCard;

import com.goldstone.saboteur_backend.domain.board.Board;
import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.card.actionCard.BreakToolCard;
import com.goldstone.saboteur_backend.domain.enums.ActionCardType;
import com.goldstone.saboteur_backend.domain.enums.TargetToolType;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.domain.user.UserCardDeck;
import com.goldstone.saboteur_backend.session.GlobalSession;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ActionCardServiceTest {

    private final GlobalSession globalSession;

    @PostConstruct
    public void init() {
        // 유저 생성
        User user = new User();
        user.setId(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"));
        user.setNickname("테스터");
        user.initToolStatus();


        // 대상 유저
        User targetUser = new User();
        targetUser.setId(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"));
        targetUser.setNickname("타겟");
        targetUser.initToolStatus();


        // 게임룸 생성
        GameRoom gameRoom = new GameRoom();
        UUID gameRoomId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        gameRoom.setId(gameRoomId);
        Board board = new Board();

        // 카드 생성
        BreakToolCard card = new BreakToolCard(TargetToolType.PICKAX);
        UUID cardId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        System.out.println("Card ID before setting: " + card.getCardId());

        card.setCardId(cardId);

        System.out.println("Card ID after setting: " + card.getCardId());

        // 사용자 핸드에 카드 추가
        List<Card> cardList = new ArrayList<>();
        cardList.add(card);

        UserCardDeck userDeck = new UserCardDeck(user, cardList);
        user.setCardDeck(userDeck);

        globalSession.addUserSession(user);
        globalSession.addUserSession(targetUser);
        globalSession.addGameRoomSession(gameRoom);
        globalSession.addGameBoardSession(gameRoom, board);

        User sessionUser = globalSession.getUserSession(user.getId());
        System.out.println("Session user found: " + (sessionUser != null));
        if (sessionUser != null) {
            System.out.println("Session user deck: " + (sessionUser.getCardDeck() != null));
            if (sessionUser.getCardDeck() != null) {
                System.out.println("Session user deck cards size: " + sessionUser.getCardDeck().getCards().size());
                if (sessionUser.getCardDeck().getCards().size() > 0) {
                    System.out.println("Session user deck first card ID: " + sessionUser.getCardDeck().getCards().get(0).getCardId());
                }
            }
        }

        Card foundCard = sessionUser.getCardDeck().getCardById(cardId);
        System.out.println("Found card by ID: " + (foundCard != null ? foundCard.getCardId() : "null"));
    }
}
