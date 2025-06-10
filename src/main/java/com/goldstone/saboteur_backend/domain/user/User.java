package com.goldstone.saboteur_backend.domain.user;

import com.goldstone.saboteur_backend.domain.card.GoldCard;
import com.goldstone.saboteur_backend.domain.common.BaseEntity;
import com.goldstone.saboteur_backend.domain.enums.PlayerToolStatus;
import com.goldstone.saboteur_backend.domain.enums.TargetToolType;
import com.goldstone.saboteur_backend.domain.enums.UserStatus;
import com.goldstone.saboteur_backend.domain.mapping.UserGameLog;
import com.goldstone.saboteur_backend.domain.mapping.UserGameRoom;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
    @Id private UUID id;

    private LocalDate birthDate;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVATED;

    @OneToMany(mappedBy = "user")
    private List<UserGameLog> userGameLogs;

    @OneToMany(mappedBy = "user")
    private List<UserGameRoom> userGameRooms;

    @Setter @Transient private UserCardDeck cardDeck = new UserCardDeck();

    @Transient
    private Map<TargetToolType, PlayerToolStatus> toolStatusMap =
            new EnumMap<>(TargetToolType.class);

    @Transient private List<GoldCard> goldCards = new ArrayList<>();

    @Transient private int goldScore = 0;

    public void addGoldCard(GoldCard goldCard) {
        this.goldCards.add(goldCard);
        this.goldScore += goldCard.getAmount();
    }

    public User(String nickname, LocalDate birthDate) {
        this.id = UUID.randomUUID();
        this.nickname = nickname;
        this.birthDate = birthDate;
    }

    public void initToolStatus() {
        for (TargetToolType tool : TargetToolType.values()) {
            toolStatusMap.put(tool, PlayerToolStatus.FIXED);
        }
    }

    public boolean areAllToolsFixed() {
        for (PlayerToolStatus staus : toolStatusMap.values()) {
            if (staus != PlayerToolStatus.FIXED) return false;
        }
        return true;
    }

    public void breakTool(TargetToolType toolType) {
        toolStatusMap.put(toolType, PlayerToolStatus.BROKEN);
    }

    public void repairTools(Set<TargetToolType> tools) {
        for (TargetToolType tool : tools) {
            toolStatusMap.put(tool, PlayerToolStatus.FIXED);
        }
    }

    public boolean canPlacePlathCard() {
        for (PlayerToolStatus status : toolStatusMap.values()) {
            if (status.equals(PlayerToolStatus.BROKEN)) {
                return false;
            }
        }
        return true;
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
