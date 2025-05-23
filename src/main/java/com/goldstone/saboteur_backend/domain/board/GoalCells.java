package com.goldstone.saboteur_backend.domain.board;

import com.goldstone.saboteur_backend.domain.card.GoalCard;
import com.goldstone.saboteur_backend.domain.enums.GoalCardType;
import com.goldstone.saboteur_backend.domain.enums.PathCardType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GoalCells {
    private final GoalCard goldGoal;
    private final GoalCard emptyGoal1;
    private final GoalCard emptyGoal2;

    public GoalCells() {
        this.goldGoal = new GoalCard(GoalCardType.GOLD, PathCardType.CROSSROAD);
        this.emptyGoal1 = new GoalCard(GoalCardType.EMPTY, PathCardType.LEFT_TURN);
        this.emptyGoal2 = new GoalCard(GoalCardType.EMPTY, PathCardType.RIGHT_TURN);
    }

    public List<GoalCard> shuffleGoals() {
        // 목적지 섞기
        List<GoalCard> shuffledGoals = Arrays.asList(goldGoal, emptyGoal1, emptyGoal2);
        Collections.shuffle(shuffledGoals);
        return shuffledGoals;
    }
}
