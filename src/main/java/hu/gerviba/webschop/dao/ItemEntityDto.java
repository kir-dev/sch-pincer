package hu.gerviba.webschop.dao;

import java.util.ArrayList;
import java.util.List;

import hu.gerviba.webschop.model.ItemEntity;
import hu.gerviba.webschop.model.ItemOrderableStatus;
import hu.gerviba.webschop.model.OpeningEntity;
import hu.gerviba.webschop.model.TimeWindowEntity;
import lombok.Data;

@Data
public class ItemEntityDto {
    
    private final Long id;
    private final String name;
    private final String description;
    private final String ingredients;
    private final String detailsConfigJson;
    private final int price; 
    private final boolean orderable;
    private final boolean service;
    private final boolean personallyOrderable;
    private final String imageName;
    private final Long circleId;
    private final String circleAlias;
    private final String circleName;
    private final String circleColor;
    private final long nextOpeningDate;
    private final List<TimeWindowEntity> timeWindows;
    private final ItemOrderableStatus orderStatus;
    private final int flag;
    
    public ItemEntityDto(ItemEntity base, OpeningEntity opening, boolean loggedin) {
        this.id = base.getId();
        this.name = base.getName();
        this.description = base.getDescription();
        this.ingredients = base.getIngredients();
        this.detailsConfigJson = base.getDetailsConfigJson();
        this.price = loggedin ? base.getPrice() : -1;
        this.orderable = base.isOrderable() && opening.isInInterval(System.currentTimeMillis());
        this.service = base.isService();
        this.personallyOrderable = base.isPersonallyOrderable();
        this.imageName = base.getImageName();
        this.nextOpeningDate = opening == null ? 0 : opening.getDateStart();
        this.timeWindows = opening == null ? new ArrayList<>() : opening.getTimeWindows();
        this.orderStatus = ItemOrderableStatus.OK;
        this.flag = base.getFlag();
        
        if (base.getCircle() != null) {
		    this.circleId = base.getCircle().getId();
		    this.circleAlias = base.getCircle().getAlias();
		    this.circleName = base.getCircle().getDisplayName();
		    this.circleColor = base.getCircle().getCssClassName();
        } else {
		    this.circleId = 0L;
            this.circleAlias = "404";
		    this.circleName = "Not Attached";
		    this.circleColor = "";
        }
    }
    
}
