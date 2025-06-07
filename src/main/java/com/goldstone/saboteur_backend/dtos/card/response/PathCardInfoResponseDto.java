package com.goldstone.saboteur_backend.dtos.card.response;

import com.goldstone.saboteur_backend.domain.card.GoalCard;
import com.goldstone.saboteur_backend.domain.card.PathCard;
import com.goldstone.saboteur_backend.domain.enums.GoalCardType;
import com.goldstone.saboteur_backend.domain.enums.PathCardType;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class PathCardInfoResponseDto extends CardInfoResponseDto {
    private final PathCardType pathCardType;
    private final boolean rotated;
    // TODO: Getter 때문에 isGoalCard가 아니라 goalCard로 자동으로 변경됨. 어떻게 해결해야할지 고민 필요.
    private final boolean isGoalCard;
    private final GoalCardType goalCardType;

    public static PathCardInfoResponseDto from(PathCard card) {
        PathCardInfoResponseDtoBuilder<?, ?> builder = PathCardInfoResponseDto.builder();

        builder.id(card.getCardId().toString())
                .pathCardType(card.getPathCardType())
                .rotated(card.isRotated())
                .isGoalCard(false);

        if (card instanceof GoalCard goalCard) {
            builder.isGoalCard(true).goalCardType(goalCard.getType());
        }

        return builder.build();
    }
}
