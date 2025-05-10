package com.goldstone.saboteur_backend.domain;

import com.goldstone.saboteur_backend.domain.enums.GameRole;
import com.goldstone.saboteur_backend.domain.mapping.UserGameRole;
import com.goldstone.saboteur_backend.domain.mapping.UserGameRoom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GameRoleAssignment {
    // NOTE: Pair { Saboteur Num, Miner Num }
    private int[][] ROLE_CARD_PAIR = {
        {0, 0}, {0, 0}, {0, 0}, {1, 3}, {1, 4}, {2, 4}, {2, 5}, {3, 5}, {3, 6}, {3, 7}, {4, 7}
    };

    public List<UserGameRole> assignRoles(
            GameRoom gameRoom, UserGameRoom userGameRoom, List<User> users, Integer round) {
        List<UserGameRole> result = new ArrayList<>();

        int SABOTEUR_NUM = this.ROLE_CARD_PAIR[users.size()][0];
        int MINER_NUM = this.ROLE_CARD_PAIR[users.size()][1];

        List<GameRole> roles = new ArrayList<>();
        for (int i = 0; i < SABOTEUR_NUM; i++) roles.add(GameRole.SABOTEUR);
        for (int i = 0; i < MINER_NUM; i++) roles.add(GameRole.MINER);

        Collections.shuffle(roles);

        for (int i = 0; i < users.size(); i++) {
            result.add(new UserGameRole(gameRoom, userGameRoom, users.get(i), roles.get(i), round));
        }

        return result;
    }
}
