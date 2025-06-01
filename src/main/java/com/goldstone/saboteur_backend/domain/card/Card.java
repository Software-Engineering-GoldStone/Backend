package com.goldstone.saboteur_backend.domain.card;

import com.goldstone.saboteur_backend.domain.board.Cell;
import com.goldstone.saboteur_backend.domain.user.User;
import java.util.UUID;

public abstract class Card {
    private UUID id = UUID.randomUUID();

    public UUID getId() {
        return id;
    }

    public void use() throws Exception {
        throw new Exception("기본 카드 타입은 대상 없는 사용을 지원하지 않습니다. 대상 객체를 명시하세요.");
    }

    public void use(User targetUser) throws Exception {
        throw new Exception("이 카드는 사용자(User)를 대상으로 사용할 수 없습니다.");
    }

    public void use(Cell targetCell) throws Exception {
        throw new Exception("이 카드는 사용자(User)를 대상으로 사용할 수 없습니다.");
    }

    public abstract boolean availableUse();
}
