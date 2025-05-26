package com.goldstone.saboteur_backend.domain.user;

import com.goldstone.saboteur_backend.domain.common.BaseEntity;
import com.goldstone.saboteur_backend.domain.enums.UserStatus;
import com.goldstone.saboteur_backend.domain.mapping.UserGameLog;
import com.goldstone.saboteur_backend.domain.mapping.UserGameRoom;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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

    @Transient private UserCardDeck cardDeck;

    public User(String nickname, LocalDate birthDate) {
        this.id = UUID.randomUUID();
        this.nickname = nickname;
        this.birthDate = birthDate;
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
