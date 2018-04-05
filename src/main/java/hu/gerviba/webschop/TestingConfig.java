package hu.gerviba.webschop;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import hu.gerviba.webschop.model.CircleEntity;
import hu.gerviba.webschop.model.OpeningEntity;
import hu.gerviba.webschop.service.CircleService;
import hu.gerviba.webschop.service.OpeningService;

@Component
@Profile("test")
public class TestingConfig {
    
    @Autowired
    CircleService circles;
    
    @Autowired
    OpeningService openings;
    
    private static final String LONG_LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. \n" + 
            "Quisque eu nibh et mi egestas pretium at eget elit. Vestibulum et felis eget dui facilisis tincidunt.\n" + 
            "Maecenas vel nibh aliquam, luctus massa vel, venenatis elit. Integer et finibus eros. Nullam a enim \n" + 
            "luctus, volutpat nisl a, vulputate leo. Nulla facilisi. Praesent in neque eget lectus consequat \n" + 
            "euismod ut eget erat.";
    
    private static final String LONG_LOREM_IPSUM_HOME = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. \n" + 
            "Quisque eu nibh et mi egestas pretium at eget elit. Vestibulum et felis eget dui facilisis tincidunt.\n" + 
            "Maecenas vel nibh aliquam, luctus massa vel, venenatis elit. Integer et finibus eros. Nullam a enim \n" + 
            "luctus, volutpat nisl a, vulputate leo. Nulla facilisi. Praesent in neque eget lectus consequat \n" + 
            "euismod home page description ut eget erat.";
    
    @PostConstruct
    public void insertDbData() {
        CircleEntity circle;
        
        circles.add(circle = new CircleEntity("Pizzasch", LONG_LOREM_IPSUM, LONG_LOREM_IPSUM_HOME, "orange", 
                "demo/pizzasch-bg.jpg", "icons/ecommerce_bag_check.svg"));
        
        openings.add(new OpeningEntity(1525370400000L, 1525392000000L, 1525330800000L, 1525348800000L, 
                "demo/pizzasch-pr.jpg", "Jack pls mit írjak ide?", circle, 100, 20));
        
        circles.add(circle = new CircleEntity("Dzsájrosz", LONG_LOREM_IPSUM, LONG_LOREM_IPSUM_HOME, "green",
                "demo/dzsajrosz-bg.jpg", "icons/ecommerce_gift.svg"));
        
        openings.add(new OpeningEntity(1525467600000L, 1525478400000L, 1525417200000L, 1525435200000L, 
                "demo/dzsajrosz-pr.jpg", "Type your feeling here", circle, 100, 20));
        
        circles.add(circle = new CircleEntity("Americano", LONG_LOREM_IPSUM, LONG_LOREM_IPSUM_HOME, "blue", 
                "demo/americano-bg.jpg", "icons/ecommerce_graph_increase.svg"));
        
        openings.add(new OpeningEntity(1525284000000L, 1525305600000L, 1525244400000L, 1525262400000L, 
                "demo/americano-pr.jpg", "Random moment cuccok", circle, 100, 20));
        
        circles.add(circle = new CircleEntity("Vödör", LONG_LOREM_IPSUM, LONG_LOREM_IPSUM_HOME, "purple", 
                "demo/vodor-bg.jpg", "icons/ecommerce_money.svg"));
        
        openings.add(new OpeningEntity(1525197600000L, 1525219200000L, 1525158000000L, 1525176000000L, 
                "demo/dzsajrosz-pr.jpg", "Feeling típusú nyitás", circle, 100, 20));
        
        circles.add(circle = new CircleEntity("Kakas", LONG_LOREM_IPSUM, LONG_LOREM_IPSUM_HOME, "red", 
                "demo/kakas-bg.jpg", "icons/ecommerce_safe.svg"));
        
        openings.add(new OpeningEntity(1525726800000L, 152573760021000L, 1525726800000L, 1525737600000L, 
                "demo/kakas-pr.jpg", "Forradalmi nyitás", circle, 100, 20));
        
        circles.add(circle = new CircleEntity("Lángosch", LONG_LOREM_IPSUM, LONG_LOREM_IPSUM_HOME, "yellow", 
                "demo/langosch-bg.jpg", "icons/ecommerce_sale.svg"));
        
        openings.add(new OpeningEntity(1525716000000L, 1525737600000L, 1525676400000L, 1525694400000L, 
                "demo/langosch-pr.jpg", "Tüzes lángos", circle, 100, 20));
    }
    
}
