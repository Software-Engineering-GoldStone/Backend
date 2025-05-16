package com.goldstone.saboteur_backend.domain.enums;

import lombok.Getter;

@Getter
public enum PathType {
    PATH, // 통로
    ROCK, // 카드의 4면 중 막힌 부분
    EMPTY,
    DEADEND; // 미끼카드(1~9)
}
