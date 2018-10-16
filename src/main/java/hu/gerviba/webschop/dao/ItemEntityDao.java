package hu.gerviba.webschop.dao;

import hu.gerviba.webschop.model.ItemEntity;
import lombok.Data;

@Data
public class ItemEntityDao {

    private final Long id;
    private final String name;
    private final String description;
    private final String ingredients;
    private final String detailsConfigJson;
    private final int price; 
    private final boolean orderable;
    private final String imageName;
    private final Long circleId;
    private final String circleName;
    private final String circleColor;
    
    public ItemEntityDao(ItemEntity base) {
        this.id = base.getId();
        this.name = base.getName();
        this.description = base.getDescription();
        this.ingredients = base.getIngredients();
        this.detailsConfigJson = base.getDetailsConfigJson();
        this.price = base.getPrice();
        this.orderable = base.isOrderable(); // TODO: && base.getCircle() nextOpening inRange
        this.imageName = base.getImageName();
        
        if (base.getCircle() != null) {
		    this.circleId = base.getCircle().getId();
		    this.circleName = base.getCircle().getDisplayName();
		    this.circleColor = base.getCircle().getCssClassName();
        } else {
		    this.circleId = 0L;
		    this.circleName = "Not Attached";
		    this.circleColor = "";
        }
    }
    
}
