package hu.gerviba.webschop;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import hu.gerviba.webschop.model.CircleEntity;
import hu.gerviba.webschop.model.CircleMemberEntity;
import hu.gerviba.webschop.model.ItemEntity;
import hu.gerviba.webschop.model.OpeningEntity;
import hu.gerviba.webschop.model.ReviewEntity;
import hu.gerviba.webschop.service.CircleMemberService;
import hu.gerviba.webschop.service.CircleService;
import hu.gerviba.webschop.service.ItemService;
import hu.gerviba.webschop.service.OpeningService;
import hu.gerviba.webschop.service.ReviewService;

@Component
@Profile("test")
public class TestingConfig {
    
    @Autowired
    CircleService circles;
    
    @Autowired
    OpeningService openings;
    
    @Autowired
    ItemService items;
    
    @Autowired
    CircleMemberService circleMembers;
    
    @Autowired
    ReviewService reviews;
    
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
        
        circles.save(circle = new CircleEntity("Pizzásch", LONG_LOREM_IPSUM, LONG_LOREM_IPSUM_HOME, "orange", 1991,
                "demo/pizzasch-bg.jpg", "icons/icon-pizzasch.svg", "Szerda"));
        
        openings.save(new OpeningEntity(1525370400000L, 1525392000000L, 1525330800000L, 1525348800000L, 
                "demo/pizzasch-pr.jpg", "Jack pls mit írjak ide?", circle, 100, 20));
        
        circleMembers.save(new CircleMemberEntity(circle, "Valami Dezső", "Körvez", "demo/profile-pic-1.jpg", 10));
        circleMembers.save(new CircleMemberEntity(circle, "Test Elek", "Tag", "demo/profile-pic-1.jpg", 0));
        circleMembers.save(new CircleMemberEntity(circle, "Lány Név", "Gazdaságis", "demo/profile-pic-2.jpg", 5));
        circleMembers.save(new CircleMemberEntity(circle, "Valami János", "Tag", "demo/profile-pic-1.jpg", 0));
        circleMembers.save(new CircleMemberEntity(circle, "Valami Péter", "Tag", "demo/profile-pic-1.jpg", 0));
        circleMembers.save(new CircleMemberEntity(circle, "Valami Bogi", "Tag", "demo/profile-pic-2.jpg", 0));
        circleMembers.save(new CircleMemberEntity(circle, "Valami Sándor", "Tag", "demo/profile-pic-1.jpg", 0));
        circleMembers.save(new CircleMemberEntity(circle, "Valami József", "Tag", "demo/profile-pic-1.jpg", 0));
        circleMembers.save(new CircleMemberEntity(circle, "Valami Attila", "Tag", "demo/profile-pic-1.jpg", 0));
        
        reviews.save(new ReviewEntity(circle, "Szabó Gergely", LONG_LOREM_IPSUM, 
                System.currentTimeMillis(), 5, 3, 5, 5));
        reviews.save(new ReviewEntity(circle, "Kredit Huszár", "Csicskagyász ez a kör", 
                System.currentTimeMillis() - 400000000, 1, 1, 1, 1));
        reviews.save(new ReviewEntity(circle, "Tavasz Gábor", 
                "Tesz szöveg. Teszt értékelés. A kockázatok és a mellékhatások pls...",
                System.currentTimeMillis() + 332112300, 4, 5, 4, 4));
        
        items.save(new ItemEntity("Ördög Pizza", circle, 
                "Jalapeno szósz, Pick szalámi, bacon, pepperóni, mozzarella, lorem ipsum dolor sit amet", 
                "Jalapeno szósz, Pick szalámi, bacon, pepperóni, mozzarella", 
                "pizza csipos jalapeno pick bacon pepperoni mozzarella",
                "[{\"name\":\"size\",\"values\":[\"32\",\"42\",\"64\"]}]", 800, true, true, "cdn/items/1.jpg"));
        
        items.save(new ItemEntity("Songoku Pizza", circle, 
                "Paradicsomos alap, sonka, kukorica, friss gomba, mozzarella, lorem ipsum dolor sit amet",
                "Paradicsomos alap, sonka, kukorica, friss gomba, mozzarella", 
                "pizza paradicsom sonka kukorica asd song",
                "[{\"name\":\"size\"}]", 800, true, true, "cdn/items/2.jpg"));
        
        items.save(new ItemEntity("Royal w/ Cheese Pizza", circle, 
                "Bacon, sonka, paradicsom, mozzarella, sajtkrémes alap, lorem ipsum dolor sit amet", 
                "Bacon, sonka, paradicsom, mozzarella, sajtkrémes alap", 
                "pizza bacon sonka paradicsom mozzarella sajt krem",
                "[{\"name\":\"size\"}]", 800, true, true, "cdn/items/3.jpg"));
        
        circles.save(circle = new CircleEntity("Dzsájrosz", LONG_LOREM_IPSUM, LONG_LOREM_IPSUM_HOME, "green", 1995,
                "demo/dzsajrosz-bg.jpg", "icons/icon-dzsajrosz.svg", "Csütörtök"));

        items.save(new ItemEntity("Dzsájrosz pitában", circle, 
                "Pita, hagyma, saláta, paradicsom, szósz, lorem ipsum dolor sit amet", 
                "Pita, hagyma, saláta, paradicsom, szósz", 
                "gyros pita hagyma",
                "[{\"name\":\"size\",\"values\":[\"kicsi\",\"nagy\"]}]", 800, true, true, "cdn/items/5.jpg"));
        
        openings.save(new OpeningEntity(1525467600000L, 1525478400000L, 1525417200000L, 1525435200000L, 
                "demo/dzsajrosz-pr.jpg", "Type your feeling here", circle, 100, 20));
        
        circles.save(circle = new CircleEntity("Americano", LONG_LOREM_IPSUM, LONG_LOREM_IPSUM_HOME, "blue",  2002,
                "demo/americano-bg.jpg", "icons/icon-americano.svg", "Kedd"));

        items.save(new ItemEntity("Random Burger", circle, 
                "Izé, hozé, bigyó és return 4, lorem ipsum dolor sit amet", 
                "Izé, hozé, bigyó és return 4", 
                "burger asd",
                "[{\"name\":\"size\"}]", 600, true, true, "cdn/items/4.jpg"));
        
        openings.save(new OpeningEntity(1525284000000L, 1525305600000L, 1525244400000L, 1525262400000L, 
                "demo/americano-pr.jpg", "Random moment cuccok", circle, 100, 20));
        
        circles.save(circle = new CircleEntity("Vödör", LONG_LOREM_IPSUM, LONG_LOREM_IPSUM_HOME, "purple",  2005,
                "demo/vodor-bg.jpg", "icons/icon-vodor.svg", "Hétfő"));
        
        items.save(new ItemEntity("Sültkrumpli", circle, 
                "Izé, hozé, bigyó és 4, lorem ipsum dolor sit amet", 
                "Izé, hozé, bigyó és 4", 
                "sult krumpli",
                "[{\"name\":\"size\"}]", 600, true, true, "cdn/items/8.jpg"));
        
        openings.save(new OpeningEntity(1525197600000L, 1525219200000L, 1525158000000L, 1525176000000L, 
                "demo/dzsajrosz-pr.jpg", "Feeling típusú nyitás", circle, 100, 20));
        
        circles.save(circle = new CircleEntity("Kakas", LONG_LOREM_IPSUM, LONG_LOREM_IPSUM_HOME, "red", 1999,
                "demo/kakas-bg.jpg", "icons/icon-kakas.svg", "Vasárnap"));
        
        openings.save(new OpeningEntity(1525726800000L, 152573760021000L, 1525726800000L, 1525737600000L, 
                "demo/kakas-pr.jpg", "Forradalmi nyitás", circle, 100, 20));

        items.save(new ItemEntity("Sonkás melegszendvics", circle, 
                "Sonka, sajt, hagyma, vaj, lorem ipsum dolor sit amet", 
                "Sonka, sajt, hagyma, vaj", 
                "meleg szendvics sonka sajt hagyma",
                "[{\"name\":\"size\"}]", 200, true, true, "cdn/items/6.jpg"));
        
        circles.save(circle = new CircleEntity("Lángosch", LONG_LOREM_IPSUM, LONG_LOREM_IPSUM_HOME, "yellow",  1994,
                "demo/langosch-bg.jpg", "icons/icon-langosch.svg", "Vasárnap (2 hetente)"));
        
        openings.save(new OpeningEntity(1525716000000L, 1525737600000L, 1525676400000L, 1525694400000L, 
                "demo/langosch-pr.jpg", "Tüzes lángos", circle, 100, 20));

        items.save(new ItemEntity("Tüzes lángos", circle, 
                "Chili, sonka, paradicsom, mozzarella, sajtkrémes alap, lorem ipsum dolor sit amet", 
                "Chili, sonka, paradicsom, mozzarella, sajtkrémes alap", 
                "langsch langs tuzes chili",
                "[{\"name\":\"size\"}]", 500, true, true, "cdn/items/7.jpg"));
        
    }
    
}
