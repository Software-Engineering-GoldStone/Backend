package com.goldstone.saboteur_backend.domain.game;

import com.goldstone.saboteur_backend.domain.enums.GameRole;
import com.goldstone.saboteur_backend.domain.mapping.UserGameRole;
import com.goldstone.saboteur_backend.domain.mapping.UserGameRoom;
import com.goldstone.saboteur_backend.domain.user.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GameRoleAssignment {
    private static final int[][] ROLE_CARD_PAIR = {
        {0, 0}, {0, 0}, {0, 0}, {1, 2}, {1, 3}, {2, 3}, {2, 4}, {3, 4}, {3, 5}, {3, 6}, {4, 6}
    };

    public static int[][] getRoleCardPair() {
        return ROLE_CARD_PAIR;
    }

    public List<UserGameRole> assignRoles(
            GameRoom gameRoom, List<UserGameRoom> userGameRooms, Integer round) {
        List<UserGameRole> result = new ArrayList<>();
        List<User> users = userGameRooms.stream().map(UserGameRoom::getUser).toList();

        int playerCount = users.size();
        if (playerCount < 3 || playerCount > 10) {
            throw new IllegalArgumentException("Invalid player count: " + playerCount);
        }

        int saboteurCount = getRoleCardPair()[playerCount][0];
        int minerCount = getRoleCardPair()[playerCount][1];

        List<GameRole> roles = new ArrayList<>();
        for (int i = 0; i < saboteurCount; i++) roles.add(GameRole.SABOTEUR);
        for (int i = 0; i < minerCount; i++) roles.add(GameRole.MINER);
        Collections.shuffle(roles);

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            UserGameRoom userGameRoom =
                    userGameRooms.stream()
                            .filter(ugr -> ugr.getUser().equals(user))
                            .findFirst()
                            .orElseThrow();
            result.add(new UserGameRole(gameRoom, userGameRoom, user, roles.get(i), round));
        }
        return result;
    }
}
