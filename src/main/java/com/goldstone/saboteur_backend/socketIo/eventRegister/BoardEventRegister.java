package com.goldstone.saboteur_backend.socketIo.eventRegister;

import com.corundumstudio.socketio.SocketIOServer;
import com.goldstone.saboteur_backend.domain.board.Board;
import com.goldstone.saboteur_backend.domain.board.Cell;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.dtos.board.request.GetBoardInfoRequestDto;
import com.goldstone.saboteur_backend.dtos.board.request.GetReachableGoalsRequestDto;
import com.goldstone.saboteur_backend.dtos.board.response.BoardInfoResponseDto;
import com.goldstone.saboteur_backend.dtos.board.response.GetReachableGoalsResponseDto;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.ErrorCode;
import com.goldstone.saboteur_backend.exception.code.error.GameRoomErrorCode;
import com.goldstone.saboteur_backend.exception.responseDto.ErrorResponse;
import com.goldstone.saboteur_backend.service.board.BoardService;
import com.goldstone.saboteur_backend.session.GlobalSession;
import com.goldstone.saboteur_backend.socketIo.SocketIoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BoardEventRegister implements SocketEventRegister {

    private final GlobalSession globalSession;
    private final BoardService boardService;
    private final SocketIoService socketIoService;

    @Override
    public void registerEvents(SocketIOServer server) {
        // 도착지 판별
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

        // 보드 정보 반환
        server.addEventListener(
                "getBoardInfo",
                GetBoardInfoRequestDto.class,
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

                        this.socketIoService.sendBroadCast(
                                gameRoom.getId(), "boardInfo", BoardInfoResponseDto.from(board));
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
