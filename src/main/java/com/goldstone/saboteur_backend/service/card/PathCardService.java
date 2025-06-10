package com.goldstone.saboteur_backend.service.card;

import com.goldstone.saboteur_backend.domain.board.Board;
import com.goldstone.saboteur_backend.domain.board.Cell;
import com.goldstone.saboteur_backend.domain.board.PathValidator;
import com.goldstone.saboteur_backend.domain.card.PathCard;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.dtos.card.response.UseCardResponse;
import com.goldstone.saboteur_backend.dtos.game.request.PlayCardRequestDto;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.BoardErrorCode;
import com.goldstone.saboteur_backend.exception.code.error.CardErrorCode;
import com.goldstone.saboteur_backend.exception.code.error.GameRoomErrorCode;
import com.goldstone.saboteur_backend.service.board.BoardService;
import com.goldstone.saboteur_backend.session.GlobalSession;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PathCardService {
    private final GlobalSession globalSession;
    private final BoardService boardService;

    public UseCardResponse use(
            GameRoom gameRoom, User user, PathCard card, PlayCardRequestDto dto) {
        if (!user.canPlacePlathCard()) {
            throw new BusinessException(CardErrorCode.CANNOT_PLACE_CARD_BY_TOOL_BROKEN);
        }

        Board board = this.globalSession.getGameBoardSession(gameRoom.getId());
        if (board == null) {
            throw new BusinessException(GameRoomErrorCode.GAME_BOARD_NOT_FOUND);
        }

        Cell targetCell = board.getOrCreateCell(dto.getX(), dto.getY());

        if (!targetCell.isEmptyCard()) {
            throw new BusinessException(BoardErrorCode.INVALID_PATH_PLACEMENT);
        }

        if (!(PathValidator.canPlacePathCard(board, targetCell, card))) {
            throw new BusinessException(CardErrorCode.INVALID_PLACE_CARD);
        }

        targetCell.setCard(card);
        // user.getCardDeck().useCard(pathCard);

        List<Cell> reachableGoals = boardService.getReachableGoals(board);
        if (!reachableGoals.isEmpty()) {
            globalSession.setGoldFinder(gameRoom.getId(), user);
        }

        return new UseCardResponse(
                null,
                null,
                null,
                String.format("(%d, %d) 위치에 길카드가 놓였습니다.", dto.getX(), dto.getY()));
    }
}
