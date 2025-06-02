package com.goldstone.saboteur_backend.socketIo.eventRegister;

import com.corundumstudio.socketio.SocketIOServer;
import com.goldstone.saboteur_backend.domain.enums.CardType;
import com.goldstone.saboteur_backend.dtos.card.request.UseCardRequest;
import com.goldstone.saboteur_backend.dtos.card.response.UseCardResponse;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.code.error.CardErrorCode;
import com.goldstone.saboteur_backend.service.card.PathCardService;
import com.goldstone.saboteur_backend.service.card.actionCard.ActionCardService;
import com.goldstone.saboteur_backend.socketIo.SocketIoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardEventRegister implements SocketEventRegister {
    private final ActionCardService actionCardService;
    private final PathCardService pathCardService;
    private final SocketIoService socketIoService;

    @Override
    public void registerEvents(SocketIOServer server) {
        server.addEventListener(
                "useCard",
                UseCardRequest.class,
                (client, request, ackSender) -> {
                    UseCardResponse response;

                    if (request.getCardType().equals(CardType.ACTION)) {
                        response = actionCardService.useActionCard(request);
                        System.out.println("[WebSocket] 도구 카드의 useCard 요청 수신");
                    } else if (request.getCardType().equals(CardType.PATH)) {
                        response = pathCardService.use(request);
                        System.out.println("[WebSocket] 길 카드의 useCard 요청 수신");
                    } else {
                        throw new BusinessException(CardErrorCode.INVALID_CARD_TYPE);
                    }
                    socketIoService.sendBroadCast(request.getRoomId(), "cardUsed", response);

                    ackSender.sendAckData(response);
                });
    }
}
