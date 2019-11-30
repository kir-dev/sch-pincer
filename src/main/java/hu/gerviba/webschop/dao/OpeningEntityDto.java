package hu.gerviba.webschop.dao;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OpeningEntityDto {
    
    private int timeIntervals = 4;
    private String dateStart;
    private String dateEnd;
    private String orderStart;
    private String orderEnd;
    private String feeling;
    private int maxOrder = 120;
    private int maxOrderPerInterval = 15;
    private int maxExtraPerInterval = 0;
    private int intervalLength = 30;

    private int maxAlpha = 0;
    private int maxBeta = 0;
    private int maxGamma = 0;
    private int maxDelta = 0;
    private int maxLambda = 0;
    
}
