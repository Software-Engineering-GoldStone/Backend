package com.goldstone.saboteur_backend.dtos.game.request;

import java.util.UUID;
import lombok.*;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetGameStateRequestDto {
    private UUID gameRoomId;
}
