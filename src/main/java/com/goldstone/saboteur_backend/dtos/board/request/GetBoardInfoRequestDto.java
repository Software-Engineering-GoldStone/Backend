package com.goldstone.saboteur_backend.dtos.board.request;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GetBoardInfoRequestDto {
    private UUID gameRoomId;
}
