package com.goldstone.saboteur_backend.dtos.cell.response;

import com.goldstone.saboteur_backend.domain.board.Cell;
import com.goldstone.saboteur_backend.domain.card.PathCard;
import com.goldstone.saboteur_backend.domain.enums.PathType;
import com.goldstone.saboteur_backend.dtos.card.response.PathCardInfoResponseDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CellInfoResponseDto {
    private final int x;
    private final int y;
    private final PathCardInfoResponseDto card;
    private final PathType[] sides;

    public static CellInfoResponseDto from(Cell cell) {
        return CellInfoResponseDto.builder()
                .x(cell.getX())
                .y(cell.getY())
                .card(
                        cell.getCard() == null
                                ? null
                                : PathCardInfoResponseDto.from((PathCard) cell.getCard()))
                .sides(cell.getSides())
                .build();
    }
}
