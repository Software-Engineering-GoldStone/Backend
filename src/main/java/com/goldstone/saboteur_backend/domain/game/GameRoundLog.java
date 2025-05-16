package com.goldstone.saboteur_backend.domain.game;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.goldstone.saboteur_backend.domain.common.BaseEntity;
import com.goldstone.saboteur_backend.domain.enums.GameRole;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class GameRoundLog extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_room_id")
    private GameRoom gameRoom;

    private Long roundId;

    private LocalDateTime roundStartDate;
    private LocalDateTime roundEndDate;

    @Enumerated(EnumType.STRING)
    private GameRole winnerRole;

    @OneToMany(mappedBy = "roundLog")
    private List<GameRoundUserLog> userLogs;

    @ManyToOne
    @JoinColumn(name = "game_log_id")
    private GameLog gameLog;

    public String createRawLog() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Map<String, Object> logMap = new HashMap<>();
        logMap.put("gameId", gameRoom.getId());
        logMap.put("roundId", roundId);
        logMap.put("roundStartDate", roundStartDate);
        logMap.put("roundEndDate", roundEndDate);
        logMap.put("winnerRole", winnerRole);
        logMap.put("userLogs", userLogs);
        try {
            return mapper.writeValueAsString(logMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert round log to JSON", e);
        }
    }
}
