package com.goldstone.saboteur_backend.domain.card;

import com.goldstone.saboteur_backend.domain.enums.PathCardType;
import com.goldstone.saboteur_backend.domain.enums.PathType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PathCard extends Card{
    private PathCardType pathCardType;
    private boolean rotated;

    public void rotate(){
        this.rotated = !rotated;
    }

    public PathType top() { return pathCardType.getSides(rotated)[0]; }
    public PathType right() { return pathCardType.getSides(rotated)[1]; }
    public PathType bottom() { return pathCardType.getSides(rotated)[2]; }
    public PathType left() { return pathCardType.getSides(rotated)[3]; }

    public PathType[] getSides() {
        return pathCardType.getSides(rotated);
    }

    @Override
    void use() {
    }

    @Override
    boolean availableUse() {
        return false;
    }


}
