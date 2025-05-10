package com.goldstone.saboteur_backend.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class GameRoundUserLog extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "game_round_log_id")
    private GameRoundLog roundLog;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private GameRole role;

    private Integer goldCount;

    public String createRawLog() {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> logMap = new HashMap<>();
        logMap.put("user", user.getNickname());
        logMap.put("role", role.name());
        logMap.put("goldCount", goldCount);
        try {
            return mapper.writeValueAsString(logMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert raw log to JSON", e);
        }
    }
}
