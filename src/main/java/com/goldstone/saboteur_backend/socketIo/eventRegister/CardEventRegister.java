package com.goldstone.saboteur_backend.socketIo.eventRegister;

import com.corundumstudio.socketio.SocketIOServer;
import com.goldstone.saboteur_backend.dtos.card.request.CellTargetCardRequest;
import com.goldstone.saboteur_backend.dtos.card.request.PathCardRequest;
import com.goldstone.saboteur_backend.dtos.card.request.UserTargetRequest;
import com.goldstone.saboteur_backend.dtos.card.response.UseCardResponse;
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

        // 1. 도구 파괴 카드 (DESTROY)
        server.addEventListener(
                "useBreakToolCard",
                UserTargetRequest.class,
                (client, request, ackSender) -> {
                    UseCardResponse response = actionCardService.useActionCard(request);
                    socketIoService.sendBroadCast(request.getRoomId(), "cardUsed", response);
                    ackSender.sendAckData(response);
                });

        // 2. 도구 수리 카드 (REPAIR)
        server.addEventListener(
                "useRepairToolCard",
                UserTargetRequest.class,
                (client, request, ackSender) -> {
                    UseCardResponse response = actionCardService.useActionCard(request);
                    socketIoService.sendBroadCast(request.getRoomId(), "cardUsed", response);
                    ackSender.sendAckData(response);
                });

        // 3. 지도 카드 (MAP)
        server.addEventListener(
                "useMapCard",
                CellTargetCardRequest.class,
                (client, request, ackSender) -> {
                    UseCardResponse response = actionCardService.useActionCard(request);
                    socketIoService.sendBroadCast(request.getRoomId(), "cardUsed", response);
                    ackSender.sendAckData(response);
                });

        // 4. 낙석 카드 (FALLING_ROCK)
        server.addEventListener(
                "useFallingRockCard",
                CellTargetCardRequest.class,
                (client, request, ackSender) -> {
                    UseCardResponse response = actionCardService.useActionCard(request);
                    socketIoService.sendBroadCast(request.getRoomId(), "cardUsed", response);
                    ackSender.sendAckData(response);
                });

        // 5. 길카드 (PATH)
        server.addEventListener(
                "usePathCard",
                PathCardRequest.class,
                (client, request, ackSender) -> {
                    UseCardResponse response = pathCardService.use(request);
                    socketIoService.sendBroadCast(request.getRoomId(), "cardUsed", response);
                    ackSender.sendAckData(response);
                });
    }
}
