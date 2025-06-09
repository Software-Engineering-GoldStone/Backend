package com.goldstone.saboteur_backend.service.card.actionCard;

import com.goldstone.saboteur_backend.domain.board.Board;
import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.card.actionCard.*;
import com.goldstone.saboteur_backend.domain.enums.TargetToolType;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.dtos.card.request.CellTargetCardRequest;
import com.goldstone.saboteur_backend.dtos.card.request.UseCardRequest;
import com.goldstone.saboteur_backend.dtos.card.request.UserTargetRequest;
import com.goldstone.saboteur_backend.dtos.card.response.UseCardResponse;
import com.goldstone.saboteur_backend.dtos.game.request.PlayCardRequestDto;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.CardErrorCode;
import com.goldstone.saboteur_backend.exception.code.error.CommonErrorCode;
import com.goldstone.saboteur_backend.exception.code.error.UserErrorCode;
import com.goldstone.saboteur_backend.session.GlobalSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActionCardService {
    private final BreakToolCardService breakToolCardService;
    private final RepairToolCardService repairToolCardService;
    private final MapCardService mapCardService;
    private final FallingRockCardService fallingRockCardService;

    private final GlobalSession globalSession;

    // dto의 targetUser, targetTool, x, y는 nullable
    public UseCardResponse useActionCard(GameRoom gameRoom, Card card, PlayCardRequestDto dto) {


        ActionCard actionCard = (ActionCard) card;

        UseCardResponse response;

        switch (actionCard.getActionCardType()) {
            case DESTROY -> {
                if (!(card instanceof BreakToolCard breakToolCard)) {
                    throw new BusinessException(CardErrorCode.INVALID_ACTION_CARD);
                }
                User targetUser = this.globalSession.getUserSession(dto.getUserId());
                if (targetUser == null) {
                    throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
                }
                response = breakToolCardService.use(targetUser, breakToolCard);
            }
            case REPAIR -> {
                if (!(card instanceof RepairToolCard repairToolCard)) {
                    throw new BusinessException(CardErrorCode.INVALID_ACTION_CARD);
                }
                User targetUser = this.globalSession.getUserSession(dto.getUserId());
                if (targetUser == null) {
                    throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
                }
                response = repairToolCardService.use(targetUser, repairToolCard, dto.getTargetTool());
            }
            case MAP -> {
                if (!(card instanceof MapCard mapCard)) {
                    throw new BusinessException(CardErrorCode.INVALID_ACTION_CARD);
                }
                Board board = this.globalSession.getGameBoardSession(gameRoom.getId());
                response = mapCardService.use(board, mapCard, dto.getX(), dto.getY());
            }
            case FALLING_ROCK -> {
                if (!(card instanceof FallingRockCard fallingRockCard)) {
                    throw new BusinessException(CardErrorCode.INVALID_ACTION_CARD);
                }
                Board board = this.globalSession.getGameBoardSession(gameRoom.getId());
                response = fallingRockCardService.use(board, fallingRockCard, dto.getX(), dto.getY());
            }
            default -> throw new BusinessException(CardErrorCode.INVALID_CARD_TYPE);
        }

        //user.getCardDeck().useCard(actionCard);

        return response;
    }
}
