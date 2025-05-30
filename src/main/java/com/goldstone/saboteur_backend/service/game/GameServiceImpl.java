package com.goldstone.saboteur_backend.service.game;

import com.corundumstudio.socketio.SocketIOClient;
import com.goldstone.saboteur_backend.domain.card.Card;
import com.goldstone.saboteur_backend.domain.game.GameCardPool;
import com.goldstone.saboteur_backend.domain.game.GameRoom;
import com.goldstone.saboteur_backend.domain.game.GameTurnManager;
import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.domain.user.UserCardDeck;
import com.goldstone.saboteur_backend.dtos.game.request.DiscardCardRequestDto;
import com.goldstone.saboteur_backend.dtos.game.request.GetGameStateRequestDto;
import com.goldstone.saboteur_backend.dtos.game.request.NextTurnRequestDto;
import com.goldstone.saboteur_backend.dtos.game.request.PlayCardRequestDto;
import com.goldstone.saboteur_backend.dtos.game.response.GetGameStateResponseDto;
import com.goldstone.saboteur_backend.dtos.game.response.NextTurnResponseDto;
import com.goldstone.saboteur_backend.dtos.game.response.PlayCardResponseDto;
import com.goldstone.saboteur_backend.exception.code.error.GameRoomErrorCode;
import com.goldstone.saboteur_backend.exception.code.error.UserErrorCode;
import com.goldstone.saboteur_backend.session.GlobalSession;
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

            User nextUser = turnManager.nextTurn();

            NextTurnResponseDto responseDto =
                    new NextTurnResponseDto(nextUser.getId(), nextUser.getNickname(), false);
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

            for (var ugr : gameRoom.getUserGameRooms()) {
                User user = ugr.getUser();
                UserCardDeck deck = user.getCardDeck();
                playerCardCounts.put(user.getId(), deck != null ? deck.getCards().size() : 0);
                if (user.getId().equals(myUserId) && deck != null) {
                    for (Card card : deck.getCards()) {
                        myCardIds.add(card.getId());
                    }
                }
            }

            GetGameStateResponseDto responseDto =
                    new GetGameStateResponseDto(
                            currentUser.getId(),
                            currentUser.getNickname(),
                            playerCardCounts,
                            cardPool.getCards().size(),
                            myCardIds);
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
