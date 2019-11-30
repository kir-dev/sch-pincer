package hu.gerviba.webschop.dao;

import hu.gerviba.webschop.model.CircleEntity;
import hu.gerviba.webschop.model.OpeningEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CircleEntityInfo {

    private CircleEntity circleEntity;
    private OpeningEntity nextOpening;

    public CircleEntityInfo(CircleEntity circleEntity) {
        this.circleEntity = circleEntity;
    }

}
