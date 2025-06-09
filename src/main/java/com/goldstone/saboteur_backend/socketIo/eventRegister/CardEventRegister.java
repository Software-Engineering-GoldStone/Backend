package com.goldstone.saboteur_backend.socketIo.eventRegister;

import com.corundumstudio.socketio.SocketIOServer;
import com.goldstone.saboteur_backend.dtos.card.request.CellTargetCardRequest;
import com.goldstone.saboteur_backend.dtos.card.request.PathCardRequest;
import com.goldstone.saboteur_backend.dtos.card.request.UserTargetRequest;
import com.goldstone.saboteur_backend.dtos.card.response.UseCardResponse;
import com.goldstone.saboteur_backend.exception.BusinessException;
import com.goldstone.saboteur_backend.exception.responseDto.ErrorResponse;
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
        // 1. 도구 파괴 카드
//        server.addEventListener(
//                "useBreakToolCard",
//                UserTargetRequest.class,
//                (client, request, ackSender) -> {
//                    try {
//                        UseCardResponse response = actionCardService.useActionCard(request);
//                        socketIoService.sendBroadCast(request.getRoomId(), "cardUsed", response);
//                    } catch (BusinessException e) {
//                        client.sendEvent("errorEvent", new ErrorResponse(e.getErrorCode()));
//                    }
//                });
//
//        // 2. 도구 수리 카드
//        server.addEventListener(
//                "useRepairToolCard",
//                UserTargetRequest.class,
//                (client, request, ackSender) -> {
//                    try {
//                        UseCardResponse response = actionCardService.useActionCard(request);
//                        socketIoService.sendBroadCast(request.getRoomId(), "cardUsed", response);
//                    } catch (BusinessException e) {
//                        client.sendEvent("errorEvent", new ErrorResponse(e.getErrorCode()));
//                    }
//                });
//
//        // 3. 지도 카드
//        server.addEventListener(
//                "useMapCard",
//                CellTargetCardRequest.class,
//                (client, request, ackSender) -> {
//                    try {
//                        UseCardResponse response = actionCardService.useActionCard(request);
//                        client.sendEvent("cardUsed", response);
//                    } catch (BusinessException e) {
//                        client.sendEvent("errorEvent", new ErrorResponse(e.getErrorCode()));
//                    }
//                });
//
//        // 4. 낙석 카드
//        server.addEventListener(
//                "useFallingRockCard",
//                CellTargetCardRequest.class,
//                (client, request, ackSender) -> {
//                    try {
//                        UseCardResponse response = actionCardService.useActionCard(request);
//                        socketIoService.sendBroadCast(request.getRoomId(), "cardUsed", response);
//                    } catch (BusinessException e) {
//                        client.sendEvent("errorEvent", new ErrorResponse(e.getErrorCode()));
//                    }
//                });
//
//        // 5. 길 카드
//        server.addEventListener(
//                "usePathCard",
//                PathCardRequest.class,
//                (client, request, ackSender) -> {
//                    try {
//                        UseCardResponse response = pathCardService.use(request);
//                        socketIoService.sendBroadCast(request.getRoomId(), "cardUsed", response);
//                    } catch (BusinessException e) {
//                        client.sendEvent("errorEvent", new ErrorResponse(e.getErrorCode()));
//                    }
//                });
    }
}
