package com.goldstone.saboteur_backend.domain.card;

import lombok.Getter;

// 번호만 가지는 테스트용 카드 클래스 새로 생성
@Getter
public class TestCard extends Card {

    public int cardNumber;

    public TestCard(int cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getTestCardNumber() {
        return cardNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestCard testCard = (TestCard) o;
        return cardNumber == testCard.cardNumber;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(cardNumber);
    }

    @Override
    public void use() {}

    @Override
    public boolean availableUse() {
        return true;
    }
}
