package hu.gerviba.webschop.dao;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OpeningEntityDao {
    
    private int timeIntervals = 4;
    private String dateStart;
    private String dateEnd;
    private String orderStart;
    private String orderEnd;
    private String feeling;
    private int maxOrder = 120;
    private int maxOrderPerInterval = 15;
    private int intervalLength = 30;
    
}
