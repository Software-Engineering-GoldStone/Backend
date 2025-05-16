package com.goldstone.saboteur_backend.domain.user;

import com.goldstone.saboteur_backend.domain.common.BaseEntity;
import com.goldstone.saboteur_backend.domain.enums.UserLoginType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthentication extends BaseEntity {
    @Id private String id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserLoginType loginType;

    public void modifyPassword(String password) {
        this.password = password;
    }
}
