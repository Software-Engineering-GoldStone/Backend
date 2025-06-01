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
    // NOTE: Pair { Saboteur Num, Miner Num }
    private static final int[][] ROLE_CARD_PAIR = {
        {0, 0}, {0, 0}, {0, 0}, {1, 2}, {1, 3}, {2, 3}, {2, 4}, {3, 4}, {3, 5}, {3, 6}, {4, 6}
    };

    public static int[][] getRoleCardPair() {
        return ROLE_CARD_PAIR;
    }

    /**
     * 플레이어 수에 따라 사보타지/광부 역할을 무작위로 분배한다.
     *
     * @param gameRoom 게임방 객체
     * @param userGameRooms 방에 참여한 UserGameRoom 리스트
     * @param round 라운드 번호
     * @return UserGameRole 리스트 (각 유저별 역할 할당)
     */
    public List<UserGameRole> assignRoles(
            GameRoom gameRoom, List<UserGameRoom> userGameRooms, Integer round) {
        List<UserGameRole> result = new ArrayList<>();
        List<User> users = userGameRooms.stream().map(UserGameRoom::getUser).toList();

        int playerCount = users.size();
        if (playerCount < 3 || playerCount > 10) {
            throw new IllegalArgumentException("Invalid player count: " + playerCount);
        }

        int SABOTEUR_NUM = getRoleCardPair()[playerCount][0];
        int MINER_NUM = getRoleCardPair()[playerCount][1];

        List<GameRole> roles = new ArrayList<>();
        for (int i = 0; i < SABOTEUR_NUM; i++) roles.add(GameRole.SABOTEUR);
        for (int i = 0; i < MINER_NUM; i++) roles.add(GameRole.MINER);

        Collections.shuffle(roles);

        for (int i = 0; i < users.size(); i++) {
            result.add(
                    new UserGameRole(
                            gameRoom, userGameRooms.get(i), users.get(i), roles.get(i), round));
        }

        return result;
    }
}
