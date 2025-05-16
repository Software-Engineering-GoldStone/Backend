package com.goldstone.saboteur_backend.domain.board;

import com.goldstone.saboteur_backend.domain.card.GoalCard;
import com.goldstone.saboteur_backend.domain.enums.GoalCardType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Goals {
    private final GoalCard goldGoal;
    private final GoalCard emptyGoal1;
    private final GoalCard emptyGoal2;

    public Goals() {
        this.goldGoal = new GoalCard(GoalCardType.GOLD);
        this.emptyGoal1 = new GoalCard(GoalCardType.EMPTY);
        this.emptyGoal2 = new GoalCard(GoalCardType.EMPTY);
    }

    public void assignToBoard(Cell[][] cells) {
        List<GoalCard> shuffledGoals = Arrays.asList(goldGoal, emptyGoal1, emptyGoal2);
        Collections.shuffle(shuffledGoals);

        int goalX = cells[0].length - 1; // 오른쪽 끝 열

        cells[0][goalX].setCard(shuffledGoals.get(0)); // Top row
        cells[2][goalX].setCard(shuffledGoals.get(1)); // Middle row
        cells[4][goalX].setCard(shuffledGoals.get(2)); // Bottom row
    }

    public List<GoalCard> getAllGoals() {
        return Arrays.asList(goldGoal, emptyGoal1, emptyGoal2);
    }
}

