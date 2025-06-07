package com.goldstone.saboteur_backend.dtos.board.response;

import com.goldstone.saboteur_backend.domain.board.Board;
import com.goldstone.saboteur_backend.domain.board.Cell;
import com.goldstone.saboteur_backend.dtos.cell.response.CellInfoResponseDto;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardInfoResponseDto {
    private final CellInfoResponseDto[] cellInfo;

    public static BoardInfoResponseDto from(Board board) {
        Set<Cell> dynamicCells = board.getDynamicCells();

        return BoardInfoResponseDto.builder()
                .cellInfo(
                        dynamicCells.stream()
                                .map(CellInfoResponseDto::from)
                                .toArray(CellInfoResponseDto[]::new))
                .build();
    }
}
