package com.goldstone.saboteur_backend.domain.GameCardPool;

import static org.junit.jupiter.api.Assertions.*;

import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.card.TestCard;
import com.goldstone.saboteur_backend.domain.game.GameCardPool;
import com.goldstone.saboteur_backend.domain.user.User;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("GameCardPool Test")
public class GameCardPoolTest {

    private GameCardPool gameCardPool;
    private List<Card> initialCards;

    @BeforeEach
    public void setUp() {
        initialCards = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            initialCards.add(new TestCard(i)); // 40개의 unique cards 생성
        }
        gameCardPool = new GameCardPool(new LinkedList<>(initialCards));
    }

    @Test
    @DisplayName("카드 shuffle 정상 작동 검증")
    public void testShuffleCards() {
        List<Card> previousShuffle = new ArrayList<>(initialCards);
        for (int i = 0; i < 5; i++) {
            gameCardPool.shuffleCards();
            List<Card> currentShuffle = new ArrayList<>(gameCardPool.getCards());

            // 두 리스트가 순서, 내용(요소), 크기 중 하나라도 다르면 not equal
            assertNotEquals(previousShuffle, currentShuffle, "shuffle 이후 cards의 순서가 바뀌어야 함.");

            assertTrue(
                    currentShuffle.containsAll(previousShuffle)
                            && previousShuffle.containsAll(currentShuffle),
                    "shuffle 이후의 카드 풀에 이전 shuffle의 카드 풀의 모든 카드(요소)가 포함되어야 함.");

            previousShuffle = currentShuffle;
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 4, 5, 6, 7})
    @DisplayName("카드 분배 정상 작동 검증")
    public void testAssignCards(int cardsPerPlayer) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            users.add(new User());
        }

        gameCardPool.assignCards(users, cardsPerPlayer);

        for (User user : users) {
            assertNotNull(user.getCardDeck(), "User의 card deck 은 not null 이어야 함.");
            assertEquals(
                    cardsPerPlayer,
                    user.getCardDeck().getCards().size(),
                    "Each user should have the correct number of cards assigned.");
        }

        // Verify that each user's card deck is different
        for (int i = 0; i < users.size(); i++) {
            for (int j = i + 1; j < users.size(); j++) {
                assertNotEquals(
                        users.get(i).getCardDeck().getCards(),
                        users.get(j).getCardDeck().getCards(),
                        "Each user's card deck should be different.");
            }
        }
    }
}
