package hu.gerviba.webschop.dao;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public final class OrderDetails {

    private int minCount;
    private int maxCount;

}
