package com.goldstone.saboteur_backend.dtos.board.request;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GetGoalCellInfoRequestDto {
    private UUID gameRoomId;
}
