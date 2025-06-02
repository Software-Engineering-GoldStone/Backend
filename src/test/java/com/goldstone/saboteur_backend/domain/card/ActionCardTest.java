package com.goldstone.saboteur_backend.domain.card;

import static org.junit.jupiter.api.Assertions.*;

import com.goldstone.saboteur_backend.domain.board.Board;
import com.goldstone.saboteur_backend.domain.board.Cell;
import com.goldstone.saboteur_backend.domain.card.actionCard.BreakToolCard;
import com.goldstone.saboteur_backend.domain.card.actionCard.FallingRockCard;
import com.goldstone.saboteur_backend.domain.card.actionCard.MapCard;
import com.goldstone.saboteur_backend.domain.card.actionCard.RepairToolCard;
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
        // 유저 생성 후, 카트와 랜턴을 고장난 상태로 변경
        User user = new User();
        user.initToolStatus();
        user.breakTool(TargetToolType.CART);
        user.breakTool(TargetToolType.LIGHT);

        //카트와 랜턴이 고장난 상태인지 확인
        assertEquals(PlayerToolStatus.BROKEN, user.getToolStatusMap().get(TargetToolType.CART));
        assertEquals(PlayerToolStatus.BROKEN, user.getToolStatusMap().get(TargetToolType.LIGHT));

        //한 장으로 카트와 랜턴을 수리할 수 있는 카드 생성
        Set<TargetToolType> repairTools = new HashSet<>();
        repairTools.add(TargetToolType.CART);
        repairTools.add(TargetToolType.LIGHT);
        RepairToolCard repairToolCard = new RepairToolCard(repairTools);

        //게임 룰에 따라 수리할 도구를 하나만(카트) 선택
        repairToolCard.selectTool(TargetToolType.CART);
        repairToolCard.use(user);

        //선택한 카드만 수리되고 랜턴은 고장난 상태인지 확인
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
