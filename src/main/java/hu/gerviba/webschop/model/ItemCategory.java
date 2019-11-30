package hu.gerviba.webschop.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ItemCategory {
    DEFAULT(0),
    ALPHA(1),
    BETA(2),
    GAMMA(3),
    DELTA(4),
    LAMBDA(5);

    @Getter
    final int id;

    public static ItemCategory of(Integer category) {
        for (ItemCategory value : values()) {
            if (value.getId() == category)
                return value;
        }
        return DEFAULT;
    }
}
