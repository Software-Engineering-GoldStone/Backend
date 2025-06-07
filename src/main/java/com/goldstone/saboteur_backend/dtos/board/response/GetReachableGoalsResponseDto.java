package com.goldstone.saboteur_backend.dtos.board.response;

import com.goldstone.saboteur_backend.domain.board.Cell;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.dtos.cell.response.CellInfoResponseDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetReachableGoalsResponseDto {
    private final String gameRoomId;
    private final CellInfoResponseDto[] reachableGoals;

    public static GetReachableGoalsResponseDto of(GameRoom gameRoom, List<Cell> reachableGoals) {
        return GetReachableGoalsResponseDto.builder()
                .gameRoomId(gameRoom.getId().toString())
                .reachableGoals(
                        reachableGoals.stream()
                                .map(CellInfoResponseDto::from)
                                .toArray(CellInfoResponseDto[]::new))
                .build();
    }
}
