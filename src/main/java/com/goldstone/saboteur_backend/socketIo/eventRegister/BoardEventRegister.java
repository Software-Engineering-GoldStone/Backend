package com.goldstone.saboteur_backend.socketIo.eventRegister;

import com.corundumstudio.socketio.SocketIOServer;
import com.goldstone.saboteur_backend.domain.board.Board;
import com.goldstone.saboteur_backend.domain.board.Cell;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.dtos.board.request.GetReachableGoalsRequestDto;
import com.goldstone.saboteur_backend.dtos.board.response.GetReachableGoalsResponseDto;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.ErrorCode;
import com.goldstone.saboteur_backend.exception.code.error.GameRoomErrorCode;
import com.goldstone.saboteur_backend.exception.responseDto.ErrorResponse;
import com.goldstone.saboteur_backend.service.board.BoardService;
import com.goldstone.saboteur_backend.session.GlobalSession;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BoardEventRegister implements SocketEventRegister {

    private final GlobalSession globalSession;
    private final BoardService boardService;

    @Override
    public void registerEvents(SocketIOServer server) {
        server.addEventListener(
                "getReachableGoals",
                GetReachableGoalsRequestDto.class,
                (client, request, ackSender) -> {
                    try {
                        GameRoom gameRoom =
                                this.globalSession.getGameRoomSession(request.getGameRoomId());
                        if (gameRoom == null) {
                            throw new BusinessException(GameRoomErrorCode.GAME_ROOM_NOT_FOUND);
                        }
                        Board board = this.globalSession.getGameBoardSession(gameRoom.getId());
                        if (board == null) {
                            throw new BusinessException(GameRoomErrorCode.GAME_BOARD_NOT_FOUND);
                        }

                        List<Cell> result = this.boardService.getReachableGoals(board);

                        client.sendEvent(
                                "reachableGoals",
                                GetReachableGoalsResponseDto.of(gameRoom, result));
                    } catch (Exception e) {
                        if (e instanceof BusinessException) {
                            ErrorCode errorCode = ((BusinessException) e).getErrorCode();
                            client.sendEvent("errorEvent", new ErrorResponse(errorCode));
                        } else {
                            client.sendEvent("errorEvent", ErrorResponse.internalServerError());
                        }
                    }
                });
    }
}
