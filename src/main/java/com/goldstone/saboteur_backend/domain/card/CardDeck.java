package com.goldstone.saboteur_backend.domain.card;

import com.goldstone.saboteur_backend.domain.Cell;
import com.goldstone.saboteur_backend.domain.enums.ActionCardType;
import com.goldstone.saboteur_backend.domain.enums.GoalCardType;
import lombok.Getter;

import java.util.*;

@Getter
public class CardDeck {
    private final List<Card> cards;

    public CardDeck() {
        this.cards = new ArrayList<>();
        initializeDeck();
        shuffle();
    }

    private void initializeDeck() {
        // 목표 카드
        cards.add(new GoalCard("goal_gold", "goal_gold", GoalCardType.GOLD));
        cards.add(new GoalCard("goal_rock_1", "goal_rock_1", GoalCardType.ROCK));
        cards.add(new GoalCard("goal_rock_2", "goal_rock_2", GoalCardType.ROCK));

        // 주요 path/deadend 카드들 (상,우,하,좌)
        cards.add(new PathCard("path_crossroad_1", "path_crossroad_1", new Cell.Side[]{Cell.Side.PATH, Cell.Side.PATH, Cell.Side.PATH, Cell.Side.PATH}));
        cards.add(new PathCard("path_crossroad_2", "path_crossroad_2", new Cell.Side[]{Cell.Side.PATH, Cell.Side.PATH, Cell.Side.PATH, Cell.Side.PATH}));
        cards.add(new PathCard("path_crossroad_3", "path_crossroad_3", new Cell.Side[]{Cell.Side.PATH, Cell.Side.PATH, Cell.Side.PATH, Cell.Side.PATH}));
        cards.add(new PathCard("path_crossroad_4", "path_crossroad_4", new Cell.Side[]{Cell.Side.PATH, Cell.Side.PATH, Cell.Side.PATH, Cell.Side.PATH}));
        cards.add(new PathCard("path_crossroad_5", "path_crossroad_5", new Cell.Side[]{Cell.Side.PATH, Cell.Side.PATH, Cell.Side.PATH, Cell.Side.PATH}));

        // 수평 일자
        cards.add(new PathCard("path_horizontal_1", "path_horizontal_1", new Cell.Side[]{Cell.Side.DEADEND, Cell.Side.PATH, Cell.Side.DEADEND, Cell.Side.PATH}));
        cards.add(new PathCard("path_horizontal_2", "path_horizontal_2", new Cell.Side[]{Cell.Side.DEADEND, Cell.Side.PATH, Cell.Side.DEADEND, Cell.Side.PATH}));
        cards.add(new PathCard("path_horizontal_3", "path_horizontal_3", new Cell.Side[]{Cell.Side.DEADEND, Cell.Side.PATH, Cell.Side.DEADEND, Cell.Side.PATH}));

        // 수직 일자
        cards.add(new PathCard("path_vertical_1", "path_vertical_1", new Cell.Side[]{Cell.Side.PATH, Cell.Side.DEADEND, Cell.Side.PATH, Cell.Side.DEADEND}));
        cards.add(new PathCard("path_vertical_2", "path_vertical_2", new Cell.Side[]{Cell.Side.PATH, Cell.Side.DEADEND, Cell.Side.PATH, Cell.Side.DEADEND}));
        cards.add(new PathCard("path_vertical_3", "path_vertical_3", new Cell.Side[]{Cell.Side.PATH, Cell.Side.DEADEND, Cell.Side.PATH, Cell.Side.DEADEND}));
        cards.add(new PathCard("path_vertical_4", "path_vertical_4", new Cell.Side[]{Cell.Side.PATH, Cell.Side.DEADEND, Cell.Side.PATH, Cell.Side.DEADEND}));

        // 코너 (왼쪽위, 오른쪽위, 왼쪽아래, 오른쪽아래)
        cards.add(new PathCard("path_left_1", "path_left_1", new Cell.Side[]{Cell.Side.PATH, Cell.Side.DEADEND, Cell.Side.DEADEND, Cell.Side.PATH}));
        cards.add(new PathCard("path_left_2", "path_left_2", new Cell.Side[]{Cell.Side.PATH, Cell.Side.DEADEND, Cell.Side.DEADEND, Cell.Side.PATH}));
        cards.add(new PathCard("path_left_3", "path_left_3", new Cell.Side[]{Cell.Side.PATH, Cell.Side.DEADEND, Cell.Side.DEADEND, Cell.Side.PATH}));
        cards.add(new PathCard("path_left_4", "path_left_4", new Cell.Side[]{Cell.Side.PATH, Cell.Side.DEADEND, Cell.Side.DEADEND, Cell.Side.PATH}));
        cards.add(new PathCard("path_left_5", "path_left_5", new Cell.Side[]{Cell.Side.PATH, Cell.Side.DEADEND, Cell.Side.DEADEND, Cell.Side.PATH}));

        cards.add(new PathCard("path_right_1", "path_right_1", new Cell.Side[]{Cell.Side.PATH, Cell.Side.PATH, Cell.Side.DEADEND, Cell.Side.DEADEND}));
        cards.add(new PathCard("path_right_2", "path_right_2", new Cell.Side[]{Cell.Side.PATH, Cell.Side.PATH, Cell.Side.DEADEND, Cell.Side.DEADEND}));
        cards.add(new PathCard("path_right_3", "path_right_3", new Cell.Side[]{Cell.Side.PATH, Cell.Side.PATH, Cell.Side.DEADEND, Cell.Side.DEADEND}));
        cards.add(new PathCard("path_right_4", "path_right_4", new Cell.Side[]{Cell.Side.PATH, Cell.Side.PATH, Cell.Side.DEADEND, Cell.Side.DEADEND}));

        // T자 (상,우,하,좌)
        cards.add(new PathCard("path_horizontal_t_1", "path_horizontal_t_1", new Cell.Side[]{Cell.Side.DEADEND, Cell.Side.PATH, Cell.Side.PATH, Cell.Side.PATH}));
        cards.add(new PathCard("path_horizontal_t_2", "path_horizontal_t_2", new Cell.Side[]{Cell.Side.DEADEND, Cell.Side.PATH, Cell.Side.PATH, Cell.Side.PATH}));
        cards.add(new PathCard("path_horizontal_t_3", "path_horizontal_t_3", new Cell.Side[]{Cell.Side.DEADEND, Cell.Side.PATH, Cell.Side.PATH, Cell.Side.PATH}));
        cards.add(new PathCard("path_horizontal_t_4", "path_horizontal_t_4", new Cell.Side[]{Cell.Side.DEADEND, Cell.Side.PATH, Cell.Side.PATH, Cell.Side.PATH}));
        cards.add(new PathCard("path_horizontal_t_5", "path_horizontal_t_5", new Cell.Side[]{Cell.Side.DEADEND, Cell.Side.PATH, Cell.Side.PATH, Cell.Side.PATH}));

        cards.add(new PathCard("path_vertical_t_1", "path_vertical_t_1", new Cell.Side[]{Cell.Side.PATH, Cell.Side.PATH, Cell.Side.DEADEND, Cell.Side.PATH}));
        cards.add(new PathCard("path_vertical_t_2", "path_vertical_t_2", new Cell.Side[]{Cell.Side.PATH, Cell.Side.PATH, Cell.Side.DEADEND, Cell.Side.PATH}));
        cards.add(new PathCard("path_vertical_t_3", "path_vertical_t_3", new Cell.Side[]{Cell.Side.PATH, Cell.Side.PATH, Cell.Side.DEADEND, Cell.Side.PATH}));
        cards.add(new PathCard("path_vertical_t_4", "path_vertical_t_4", new Cell.Side[]{Cell.Side.PATH, Cell.Side.PATH, Cell.Side.DEADEND, Cell.Side.PATH}));
        cards.add(new PathCard("path_vertical_t_5", "path_vertical_t_5", new Cell.Side[]{Cell.Side.PATH, Cell.Side.PATH, Cell.Side.DEADEND, Cell.Side.PATH}));

        // deadend
        cards.add(new PathCard("deadend_both_horizontal", "deadend_both_horizontal", new Cell.Side[]{Cell.Side.DEADEND, Cell.Side.PATH, Cell.Side.DEADEND, Cell.Side.PATH}));
        cards.add(new PathCard("deadend_both_vertical", "deadend_both_vertical", new Cell.Side[]{Cell.Side.PATH, Cell.Side.DEADEND, Cell.Side.PATH, Cell.Side.DEADEND}));
        cards.add(new PathCard("deadend_crossroad", "deadend_crossroad", new Cell.Side[]{Cell.Side.DEADEND, Cell.Side.PATH, Cell.Side.PATH, Cell.Side.PATH}));
        cards.add(new PathCard("deadend_horizontal_t", "deadend_horizontal_t", new Cell.Side[]{Cell.Side.PATH, Cell.Side.PATH, Cell.Side.DEADEND, Cell.Side.PATH}));
        cards.add(new PathCard("deadend_left", "deadend_left", new Cell.Side[]{Cell.Side.PATH, Cell.Side.DEADEND, Cell.Side.PATH, Cell.Side.DEADEND}));
        cards.add(new PathCard("deadend_right", "deadend_right", new Cell.Side[]{Cell.Side.PATH, Cell.Side.PATH, Cell.Side.PATH, Cell.Side.DEADEND}));
        cards.add(new PathCard("deadend_single_horizontal", "deadend_single_horizontal", new Cell.Side[]{Cell.Side.DEADEND, Cell.Side.PATH, Cell.Side.DEADEND, Cell.Side.DEADEND}));
        cards.add(new PathCard("deadend_vertical", "deadend_vertical", new Cell.Side[]{Cell.Side.DEADEND, Cell.Side.DEADEND, Cell.Side.PATH, Cell.Side.DEADEND}));

        // ... 모든 path_*, deadend_* 카드에 대해 Side 배열을 실제 이미지 연결에 맞게 추가해야 함

        // 액션 카드 (이미지명에 맞게)
        cards.add(new ActionCard("block_cart", "block_cart", ActionCardType.BREAK_CART));
        cards.add(new ActionCard("block_lantern", "block_lantern", ActionCardType.BREAK_LAMP));
        cards.add(new ActionCard("block_pickaxe", "block_pickaxe", ActionCardType.BREAK_PICK));
        cards.add(new ActionCard("repair_cart", "repair_cart", ActionCardType.FIX_CART));
        cards.add(new ActionCard("repair_lantern", "repair_lantern", ActionCardType.FIX_LAMP));
        cards.add(new ActionCard("repair_pickaxe", "repair_pickaxe", ActionCardType.FIX_PICK));
        cards.add(new ActionCard("repair_cart_lantern", "repair_cart_lantern", ActionCardType.FIX_LAMP_CART));
        cards.add(new ActionCard("repair_cart_pickaxe", "repair_cart_pickaxe", ActionCardType.FIX_PICK_CART));
        cards.add(new ActionCard("repair_lantern_pickaxe", "repair_lantern_pickaxe", ActionCardType.FIX_PICK_LAMP));
        cards.add(new ActionCard("map", "map", ActionCardType.MAP));
        cards.add(new ActionCard("rockfall", "rockfall", ActionCardType.COLLAPSE));

    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (cards.isEmpty()) return null;
        return cards.remove(cards.size() - 1);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

}
