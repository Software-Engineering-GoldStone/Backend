package com.goldstone.saboteur_backend.service.game;

import com.corundumstudio.socketio.SocketIOClient;
import com.goldstone.saboteur_backend.domain.board.Board;
import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.card.GoldCard;
import com.goldstone.saboteur_backend.domain.enums.GameRole;
import com.goldstone.saboteur_backend.domain.game.GameCardPool;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.domain.game.GameTurnManager;
import com.goldstone.saboteur_backend.domain.game.GoldCardDeck;
import com.goldstone.saboteur_backend.domain.mapping.UserGameRole;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.domain.user.UserCardDeck;
import com.goldstone.saboteur_backend.dtos.game.request.DiscardCardRequestDto;
import com.goldstone.saboteur_backend.dtos.game.request.GetGameStateRequestDto;
import com.goldstone.saboteur_backend.dtos.game.request.NextTurnRequestDto;
import com.goldstone.saboteur_backend.dtos.game.request.PlayCardRequestDto;
import com.goldstone.saboteur_backend.dtos.game.response.GetGameStateResponseDto;
import com.goldstone.saboteur_backend.dtos.game.response.NextTurnResponseDto;
import com.goldstone.saboteur_backend.dtos.game.response.PlayCardResponseDto;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.GameRoomErrorCode;
import com.goldstone.saboteur_backend.exception.code.error.UserErrorCode;
import com.goldstone.saboteur_backend.service.board.BoardService;
import com.goldstone.saboteur_backend.session.GlobalSession;
import com.goldstone.saboteur_backend.socketIo.SocketIoService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameHandleService {
    @Autowired private final GlobalSession globalSession;
    @Autowired private final SocketIoService socketIoService;
    @Autowired private final BoardService boardService;

    /* 게임 종료 조건: 카드풀이 비었고, 모든 플레이어의 손패가 0장일 때만 true
    // 또는 금 목적지 도달 시에도 true
     */
    private boolean isGameEnd(GameRoom gameRoom, GameCardPool cardPool) {
        boolean allCardsUsedUp = isCardPoolEmpty(cardPool) && areAllPlayerHandsEmpty(gameRoom);
        boolean goldGoalReached = isGoldGoalReached(gameRoom);
        return goldGoalReached || allCardsUsedUp;
    }

    private boolean isCardPoolEmpty(GameCardPool cardPool) {
        return cardPool == null || cardPool.isEmpty();
    }

    private boolean areAllPlayerHandsEmpty(GameRoom gameRoom) {
        return gameRoom.getUserGameRooms().stream()
                .allMatch(userGameRoom -> userGameRoom.getUser().getCardDeck().isEmpty());
    }

    private boolean isGoldGoalReached(GameRoom gameRoom) {
        Board board = this.globalSession.getGameBoardSession(gameRoom.getId());
        if (board == null) {
            throw new BusinessException(GameRoomErrorCode.GAME_BOARD_NOT_FOUND);
        }
        return !boardService.getReachableGoals(board).isEmpty();
    }

    /* 게임 종료 및 초기화 알림을 방 전체에 브로드캐스트
    // 역할 정보, 승리 팀, 금덩이 분배 등 포함
     */
    private void broadcastGameEnded(GameRoom gameRoom) {
        List<UserGameRole> roles = globalSession.getRoleAssignment(gameRoom.getId());
        if (roles == null) return;
        Map<UUID, String> roleMap = new HashMap<>();
        for (UserGameRole role : roles) {
            roleMap.put(role.getUser().getId(), role.getRole().name());
        }

        // 승리 팀 판단
        Board board = this.globalSession.getGameBoardSession(gameRoom.getId());
        if (board == null) {
            throw new BusinessException(GameRoomErrorCode.GAME_BOARD_NOT_FOUND);
        }
        boolean isMinerVictory = !boardService.getReachableGoals(board).isEmpty();
        String winningTeam = isMinerVictory ? "MINER" : "SABOTEUR";

        // 금덩이 분배
        GoldCardDeck goldDeck = globalSession.getGoldDeckSession(gameRoom.getId());
        if (goldDeck != null && isMinerVictory) {
            List<User> miners = new ArrayList<>();
            for (UserGameRole role : roles) {
                if (role.getRole() == GameRole.MINER) {
                    miners.add(role.getUser());
                }
            }
            List<GoldCard> golds = goldDeck.drawGoldCards(miners.size());
            for (int i = 0; i < miners.size() && i < golds.size(); i++) {
                miners.get(i).addGoldCard(golds.get(i));
            }
        }

        // 결과 브로드캐스트 (역할, 승리팀, 메시지 등)
        Map<String, Object> result = new HashMap<>();
        result.put("roles", roleMap);
        result.put("winningTeam", winningTeam);
        result.put("message", "게임이 종료되었습니다.");
        socketIoService.sendBroadCast(gameRoom.getId(), "gameEnded", result);
    }

    @Override
    public PlayCardResponseDto playCard(SocketIOClient client, PlayCardRequestDto dto)
            throws Exception {
        try {
            User user = globalSession.getUserSession(dto.getUserId());
            if (user == null) {
                throw new Exception(UserErrorCode.USER_NOT_FOUND.getMessage());
            }

            GameRoom gameRoom = globalSession.getGameRoomSession(dto.getGameRoomId());
            if (gameRoom == null) {
                throw new Exception(GameRoomErrorCode.GAME_ROOM_NOT_FOUND.getMessage());
            }

            UserCardDeck deck = user.getCardDeck();
            if (deck == null) {
                throw new Exception("유저의 카드덱이 없습니다.");
            }

            Card card =
                    deck.getCards().stream()
                            .filter(c -> c.getId().equals(dto.getCardId()))
                            .findFirst()
                            .orElseThrow(() -> new Exception("카드를 찾을 수 없습니다."));

            boolean result = deck.useCard(card);

            // 카드 사용 후 게임 종료 체크
            GameCardPool cardPool = globalSession.getGameCardPoolSession(dto.getGameRoomId());
            boolean gameEnded = isGameEnd(gameRoom, cardPool);
            if (gameEnded) {
                broadcastGameEnded(gameRoom);
            }

            PlayCardResponseDto responseDto =
                    new PlayCardResponseDto(
                            result, result ? "카드 사용 성공" : "카드 사용 실패", deck.getCards().size());
            client.sendEvent("cardPlayed", responseDto);
            return responseDto;
        } catch (Exception e) {
            client.sendEvent("error", e.getMessage());
            throw e;
        }
    }

    @Override
    public NextTurnResponseDto nextTurn(SocketIOClient client, NextTurnRequestDto dto)
            throws Exception {
        try {
            GameRoom gameRoom = globalSession.getGameRoomSession(dto.getGameRoomId());
            if (gameRoom == null) {
                throw new Exception(GameRoomErrorCode.GAME_ROOM_NOT_FOUND.getMessage());
            }

            GameTurnManager turnManager = globalSession.getTurnManagerSession(dto.getGameRoomId());
            if (turnManager == null) {
                throw new Exception("GameTurnManager를 찾을 수 없습니다.");
            }

            GameCardPool cardPool = globalSession.getGameCardPoolSession(dto.getGameRoomId());
            if (cardPool == null) {
                throw new Exception("게임 카드풀을 찾을 수 없습니다.");
            }

            User currentUser = turnManager.getCurrentTurnUser();
            UserCardDeck deck = currentUser.getCardDeck();
            if (deck == null) {
                throw new Exception("현재 플레이어의 카드덱이 없습니다.");
            }

            if (!cardPool.isEmpty()) {
                deck.addCard(cardPool.drawCard());
            }

            // 턴 넘기기 후 게임 종료 체크
            boolean gameEnded = isGameEnd(gameRoom, cardPool);
            if (gameEnded) {
                broadcastGameEnded(gameRoom);
            }

            User nextUser = turnManager.nextTurn();
            NextTurnResponseDto responseDto =
                    new NextTurnResponseDto(nextUser.getId(), nextUser.getNickname(), gameEnded);
            client.sendEvent("turnChanged", responseDto);
            return responseDto;
        } catch (Exception e) {
            client.sendEvent("error", e.getMessage());
            throw e;
        }
    }

    @Override
    public GetGameStateResponseDto getGameState(SocketIOClient client, GetGameStateRequestDto dto)
            throws Exception {
        try {
            GameRoom gameRoom = globalSession.getGameRoomSession(dto.getGameRoomId());
            if (gameRoom == null) {
                throw new Exception(GameRoomErrorCode.GAME_ROOM_NOT_FOUND.getMessage());
            }

            GameTurnManager turnManager = globalSession.getTurnManagerSession(dto.getGameRoomId());
            if (turnManager == null) {
                throw new Exception("GameTurnManager를 찾을 수 없습니다.");
            }

            GameCardPool cardPool = globalSession.getGameCardPoolSession(dto.getGameRoomId());
            if (cardPool == null) {
                throw new Exception("게임 카드풀을 찾을 수 없습니다.");
            }

            User currentUser = turnManager.getCurrentTurnUser();
            Map<UUID, Integer> playerCardCounts = new HashMap<>();
            List<UUID> myCardIds = new ArrayList<>();
            UUID myUserId = currentUser.getId();

            for (var userGameRoom : gameRoom.getUserGameRooms()) {
                User user = userGameRoom.getUser();
                UserCardDeck deck = user.getCardDeck();
                playerCardCounts.put(user.getId(), deck != null ? deck.getCards().size() : 0);
                if (user.getId().equals(myUserId) && deck != null) {
                    for (Card card : deck.getCards()) {
                        myCardIds.add(card.getId());
                    }
                }
            }

            boolean gameEnded = isGameEnd(gameRoom, cardPool);

            GetGameStateResponseDto responseDto =
                    new GetGameStateResponseDto(
                            currentUser.getId(),
                            currentUser.getNickname(),
                            playerCardCounts,
                            cardPool.getCards().size(),
                            myCardIds
                            // , gameEnded // 필요하다면 DTO에 필드 추가
                            );
            client.sendEvent("gameState", responseDto);
            return responseDto;
        } catch (Exception e) {
            client.sendEvent("error", e.getMessage());
            throw e;
        }
    }

    @Override
    public PlayCardResponseDto discardCard(SocketIOClient client, DiscardCardRequestDto dto)
            throws Exception {
        try {
            User user = globalSession.getUserSession(dto.getUserId());
            if (user == null) {
                throw new Exception(UserErrorCode.USER_NOT_FOUND.getMessage());
            }

            GameRoom gameRoom = globalSession.getGameRoomSession(dto.getGameRoomId());
            if (gameRoom == null) {
                throw new Exception(GameRoomErrorCode.GAME_ROOM_NOT_FOUND.getMessage());
            }

            UserCardDeck deck = user.getCardDeck();
            if (deck == null) {
                throw new Exception("유저의 카드덱이 없습니다.");
            }

            Card card =
                    deck.getCards().stream()
                            .filter(c -> c.getId().equals(dto.getCardId()))
                            .findFirst()
                            .orElseThrow(() -> new Exception("카드를 찾을 수 없습니다."));

            boolean result = deck.useCard(card);

            // 카드 버리기 후 게임 종료 체크
            GameCardPool cardPool = globalSession.getGameCardPoolSession(dto.getGameRoomId());
            boolean gameEnded = isGameEnd(gameRoom, cardPool);
            if (gameEnded) {
                broadcastGameEnded(gameRoom);
            }

            PlayCardResponseDto responseDto =
                    new PlayCardResponseDto(
                            result, result ? "카드 버리기 성공" : "카드 버리기 실패", deck.getCards().size());
            client.sendEvent("cardPlayed", responseDto);
            return responseDto;
        } catch (Exception e) {
            client.sendEvent("error", e.getMessage());
            throw e;
        }
    }
}
