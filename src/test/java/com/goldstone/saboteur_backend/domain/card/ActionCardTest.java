package com.goldstone.saboteur_backend.domain.card;

import static org.junit.jupiter.api.Assertions.*;

import com.goldstone.saboteur_backend.domain.board.Board;
import com.goldstone.saboteur_backend.domain.board.Cell;
import com.goldstone.saboteur_backend.domain.card.ActionCard.BreakToolCard;
import com.goldstone.saboteur_backend.domain.card.ActionCard.FallingRockCard;
import com.goldstone.saboteur_backend.domain.card.ActionCard.MapCard;
import com.goldstone.saboteur_backend.domain.card.ActionCard.RepairToolCard;
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

        Set<TargetToolType> repairTools = new HashSet<>();
        repairTools.add(TargetToolType.CART);
        repairTools.add(TargetToolType.LIGHT);

        RepairToolCard repairToolCard = new RepairToolCard(repairTools);

        repairToolCard.selectTool(TargetToolType.CART);
        repairToolCard.use(user);

        assertEquals(PlayerToolStatus.FIXED, user.getToolStatusMap().get(TargetToolType.CART));
        assertEquals(PlayerToolStatus.BROKEN, user.getToolStatusMap().get(TargetToolType.LIGHT));
    }

    @Test
    void breakTool() {
        User targetUser = new User();
        targetUser.initToolStatus();

        assertTrue(targetUser.areAllToolsFixed());

        BreakToolCard breakToolCard = new BreakToolCard(TargetToolType.CART);
        breakToolCard.use(targetUser);

        assertFalse(targetUser.areAllToolsFixed());
    }

    @Test
    void peekDestinationCard() {
        Board board = new Board();
        Cell cell = new Cell(8, 0);
        cell.setCard(new GoalCard(GoalCardType.GOLD, PathCardType.CROSSROAD));

        MapCard mapCard = new MapCard();

        GoalCardType goalCardType = mapCard.peekDestinationCard(cell);

        assertEquals(GoalCardType.GOLD, goalCardType);
        assertNotEquals(GoalCardType.EMPTY, goalCardType);
    }

    @Test
    void fallingRock() {
        Board board = new Board();
        Cell cell = new Cell(2, 1);
        cell.setCard(new PathCard(PathCardType.CROSSROAD, false));

        assertFalse(cell.isEmptyCard());

        FallingRockCard fallingRockCard = new FallingRockCard();
        fallingRockCard.use(cell);

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
