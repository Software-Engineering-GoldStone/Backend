package com.goldstone.saboteur_backend.domain.user;

import com.goldstone.saboteur_backend.domain.common.BaseEntity;
import com.goldstone.saboteur_backend.domain.enums.PlayerToolStatus;
import com.goldstone.saboteur_backend.domain.enums.TargetToolType;
import com.goldstone.saboteur_backend.domain.enums.UserStatus;
import com.goldstone.saboteur_backend.domain.mapping.UserGameLog;
import com.goldstone.saboteur_backend.domain.mapping.UserGameRoom;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // UUID는 JPA가 자동으로 생성해주지 않음

    private LocalDate birthDate;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVATED;

    @OneToMany(mappedBy = "user")
    private List<UserGameLog> userGameLogs;

    @OneToMany(mappedBy = "user")
    private List<UserGameRoom> userGameRooms;

    @Transient private UserCardDeck cardDeck;

    @Transient
    private Map<TargetToolType, PlayerToolStatus> toolStatusMap =
            new EnumMap<>(TargetToolType.class);

    public User(String nickname, LocalDate birthDate) {
        this.nickname = nickname;
        this.birthDate = birthDate;
    }

    // 플레이어의 도구 상태 초기화
    public void initToolStatus() {
        for (TargetToolType tool : TargetToolType.values()) {
            toolStatusMap.put(tool, PlayerToolStatus.FIXED);
        }
    }

    // 플레이어의 도구가 하나라도 망가져 있다면 길 카드 배치 불가능
    public boolean canePlacePathCard() {
        for (PlayerToolStatus staus : toolStatusMap.values()) {
            if (staus != PlayerToolStatus.FIXED) return false;
        }
        return true;
    }

    public void breakTool(TargetToolType toolType) {
        toolStatusMap.put(toolType, PlayerToolStatus.BROKEN);
    }

    public void repairTool(TargetToolType toolType) {
        toolStatusMap.put(toolType, PlayerToolStatus.FIXED);
    }

    public void repairTools(Set<TargetToolType> tools) {
        for (TargetToolType tool : tools) {
            toolStatusMap.put(tool, PlayerToolStatus.FIXED);
        }
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", nickname='" + nickname + "'}";
    }

    public void modifyNickname(String nickname) {
        this.nickname = nickname;
    }

    public void activateUser() {
        this.status = UserStatus.ACTIVATED;
    }

    public void deactivateUser() {
        this.status = UserStatus.DEACTIVATED;
    }

    public void deleteUser() {
        this.status = UserStatus.DELETED;
    }
}
