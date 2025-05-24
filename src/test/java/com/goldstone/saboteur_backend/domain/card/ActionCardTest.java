package com.goldstone.saboteur_backend.domain.card;

import static org.junit.jupiter.api.Assertions.*;

import com.goldstone.saboteur_backend.domain.board.Board;
import com.goldstone.saboteur_backend.domain.board.Cell;
import com.goldstone.saboteur_backend.domain.enums.*;
import com.goldstone.saboteur_backend.domain.user.User;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ActionCardTest {

    @BeforeEach
    void setUp() {}

    @AfterEach
    void tearDown() {}

    @Test
    void use() {}

    @Test
    void availableUse() {}

    @Test
    void repairTool() {
        User user = new User();
        user.initToolStatus();
        user.breakTool(TargetToolType.CART);
        user.breakTool(TargetToolType.LIGHT);

        assertEquals(PlayerToolStatus.BROKEN, user.getToolStatusMap().get(TargetToolType.CART));
        assertEquals(PlayerToolStatus.BROKEN, user.getToolStatusMap().get(TargetToolType.LIGHT));

        Set<TargetToolType> tools = new HashSet<>();
        tools.add(TargetToolType.CART);
        tools.add(TargetToolType.LIGHT);

        ActionCard actionCard = new ActionCard();
        actionCard.setType(ActionCardType.REPAIR);
        actionCard.setTools(tools);

        actionCard.setTargetUser(user);
        actionCard.use();

        assertEquals(PlayerToolStatus.FIXED, user.getToolStatusMap().get(TargetToolType.CART));
        assertEquals(PlayerToolStatus.FIXED, user.getToolStatusMap().get(TargetToolType.LIGHT));
    }

    @Test
    void destroyTool() {
        ActionCard actionCard = new ActionCard();
        actionCard.setType(ActionCardType.DESTROY);
        actionCard.setTool(TargetToolType.CART);

        User user = new User();
        user.initToolStatus();

        actionCard.setTargetUser(user);
        actionCard.use();

        assertEquals(PlayerToolStatus.BROKEN, user.getToolStatusMap().get(TargetToolType.CART));
    }

    @Test
    void peekDestinationCard() {
        Board board = new Board();
        Cell cell = new Cell(8, 0);
        cell.setCard(new GoalCard(GoalCardType.GOLD, PathCardType.CROSSROAD));

        ActionCard actionCard = new ActionCard();
        actionCard.setType(ActionCardType.MAP);

        GoalCardType goalCardType = actionCard.peekDestinationCard(cell);

        assertEquals(GoalCardType.GOLD, goalCardType);
        assertNotEquals(GoalCardType.EMPTY, goalCardType);
    }

    @Test
    void fallingRock() {
        Board board = new Board();
        Cell cell = new Cell(2, 1);
        cell.setCard(new PathCard(PathCardType.CROSSROAD, false));

        assertFalse(cell.isEmptyCard());

        ActionCard card = new ActionCard();
        card.setType(ActionCardType.FALLING_ROCK);
        card.setTargetCell(cell);
        card.use();

        assertTrue(cell.isEmptyCard());
    }

    @Test
    void getType() {}

    @Test
    void getTool() {}

    @Test
    void getTargetCell() {}

    @Test
    void getTools() {}

    @Test
    void getTargetUser() {}

    @Test
    void setType() {}

    @Test
    void setTool() {}

    @Test
    void setTargetCell() {}

    @Test
    void setTools() {}

    @Test
    void setTargetUser() {}
}
