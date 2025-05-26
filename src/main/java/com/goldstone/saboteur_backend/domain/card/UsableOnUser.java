package com.goldstone.saboteur_backend.domain.card;

import com.goldstone.saboteur_backend.domain.user.User;

public interface UsableOnUser {
    void use(User targetUser);
}
