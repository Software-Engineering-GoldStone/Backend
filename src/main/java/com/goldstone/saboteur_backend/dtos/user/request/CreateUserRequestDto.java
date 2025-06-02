package com.goldstone.saboteur_backend.dtos.user.request;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreateUserRequestDto {
    private String nickname;
    private LocalDate birthDate = LocalDate.now(); // 기본값을 현재 날짜로 설정
}
