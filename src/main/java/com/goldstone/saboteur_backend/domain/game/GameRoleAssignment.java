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
            {0, 0}, // 0명 (유효하지 않음)
            {0, 0}, // 1명 (유효하지 않음)
            {0, 0}, // 2명 (유효하지 않음)
            {1, 2}, // 3명: 사보타지 1명, 광부 2명
            {1, 3}, // 4명: 사보타지 1명, 광부 3명
            {2, 3}, // 5명: 사보타지 2명, 광부 3명
            {2, 4}, // 6명: 사보타지 2명, 광부 4명
            {3, 4}, // 7명: 사보타지 3명, 광부 4명
            {3, 5}, // 8명: 사보타지 3명, 광부 5명
            {3, 6}, // 9명: 사보타지 3명, 광부 6명
            {4, 6}  // 10명: 사보타지 4명, 광부 6명
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

        int saboteurCount = ROLE_CARD_PAIR[playerCount][0];
        int minerCount = ROLE_CARD_PAIR[playerCount][1];

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
