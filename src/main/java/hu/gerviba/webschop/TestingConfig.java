package hu.gerviba.webschop;

import java.time.Instant;

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
        
        circles.save(circle = new CircleEntity("Pizzásch", 
                "Szerda esténként az éhes kollégistákat szolgáljuk ki helybe sütött pizzáinkkal, amelyeket a "
                    + "FoodEX szállít a szobáikba. Emellett gyakran megfordulunk Gólyabálokon, bulikon, "
                    + "villanykaros és külsős rendezvényeken is.", 
                "Szerda esténként az éhes kollégistákat szolgáljuk ki helybe sütött pizzáinkkal, amelyeket a "
                    + "FoodEX szállít a szobáikba. Emellett gyakran megfordulunk Gólyabálokon, bulikon, "
                    + "villanykaros és külsős rendezvényeken is.", 
                "orange", 1991,
                "demo/pizzasch-bg.jpg", "icons/icon-pizzasch.svg", "Szerda", "pizzasch"));
        
        OpeningEntity opening;
//        Original:
        openings.save(opening = new OpeningEntity(convert(3, 18, 0), convert(4, 0, 0), convert(3, 0, 0), convert(3, 18, 0), 
                "demo/pizzasch-pr.jpg", "Ez egy Pizzásch nyitás. Ez a szöveg program sch-ra lesz exportálva.", 
                 "Jack pls mit írjak ide?", circle, 100, 20, 2, 30));
//        openings.save(opening = new OpeningEntity(convert(3, 18, 0), convert(4, 0, 0), fromNow(0), fromNow(10), 
//                "demo/pizzasch-pr.jpg", "Jack pls mit írjak ide?", circle, 100, 20, 2, 30));
        opening.generateTimeWindows(openings);
        openings.save(opening);
        
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
                "[{\"type\":\"EXTRA_SELECT\",\"name\":\"size\",\"values\":[\"32\",\"45\"],\"prices\":[0,200],\"_comment\":\"A 45-ös pizzából csak limitált mennyiségűt készítünk.\",\"_extra\":true}]",
                800, true, true, false, true, false, "cdn/items/1.jpg"));
        
        items.save(new ItemEntity("Songoku Pizza", circle, 
                "Paradicsomos alap, sonka, kukorica, friss gomba, mozzarella, lorem ipsum dolor sit amet",
                "Paradicsomos alap, sonka, kukorica, friss gomba, mozzarella", 
                "pizza paradicsom sonka kukorica asd song",
                "[{\"type\":\"EXTRA_SELECT\",\"name\":\"size\",\"values\":[\"32\",\"45\"],\"prices\":[0,200],\"_comment\":\"A 45-ös pizzából csak limitált mennyiségűt készítünk.\",\"_extra\":true}]",
                800, true, true, false, true, false, "cdn/items/2.jpg"));
        
        items.save(new ItemEntity("Royal w/ Cheese Pizza", circle, 
                "Bacon, sonka, paradicsom, mozzarella, sajtkrémes alap, lorem ipsum dolor sit amet", 
                "Bacon, sonka, paradicsom, mozzarella, sajtkrémes alap", 
                "pizza bacon sonka paradicsom mozzarella sajt krem",
                "[{\"type\":\"EXTRA_SELECT\",\"name\":\"size\",\"values\":[\"32\",\"45\"],\"prices\":[0,200],\"_comment\":\"A 45-ös pizzából csak limitált mennyiségűt készítünk.\",\"_extra\":true}]", 
                800, true, true, false, true, false, "cdn/items/3.jpg"));
        
        circles.save(circle = new CircleEntity("Dzsájrosz", 
                "Egyszer egy angol szóbelin valaki benyögte, hogy dzsájroszt reggelizett... "
                    + "biztos nem közülünk volt valaki ;) Kicsit késõbb történt, valamikor '12-ben, "
                    + "hogy a TTNY koli B404 gólya lakói sokszor fõztek szintvacsorákat, "
                    + "készítettek rengeteg palacsintát, tejberizst, de egyszer úgy döntöttek,"
                    + " hogy tartanak egy szintgyrost... Majd egy másodikat... Olyan nagy sikere volt ezeknek, "
                    + "hogy többen javasolták, csináljuk kicsit komolyabban és legyünk kajás kör. "
                    + "De mi legyen a nevünk? Ezeregyszáz sok sajttal? GyroTTNYoSCH? "
                    + "Valahogy feljött a dzsájrosz-sztori és mindenkit megnyert. "
                    + "2015 tavaszán megalakult a kör és azóta a Schönherzben tömi "
                    + "meg Dzsájrosszal az ottlakók hasát.",
                "Egyszer egy angol szóbelin valaki benyögte, hogy dzsájroszt reggelizett... "
                    + "biztos nem közülünk volt valaki ;) Kicsit késõbb történt, valamikor '12-ben, "
                    + "hogy a TTNY koli B404 gólya lakói sokszor fõztek szintvacsorákat, "
                    + "készítettek rengeteg palacsintát, tejberizst, de egyszer úgy döntöttek, "
                    + "hogy tartanak egy szintgyrost... Majd egy másodikat... Olyan nagy sikere volt ezeknek,"
                    + "hogy többen javasolták, csináljuk kicsit komolyabban és legyünk kajás kör. "
                    + "De mi legyen a nevünk? Ezeregyszáz sok sajttal? GyroTTNYoSCH? "
                    + "Valahogy feljött a dzsájrosz-sztori és mindenkit megnyert. "
                    + "2015 tavaszán megalakult a kör és azóta a Schönherzben tömi "
                    + "meg Dzsájrosszal az ottlakók hasát.", 
                "green", 1995,
                "demo/dzsajrosz-bg.jpg", "icons/icon-dzsajrosz.svg", "Csütörtök", "dzsajrosz"));

        items.save(new ItemEntity("Normál gyros tál", circle, 
                "hasábkrumpli, husi, zöldségek, öntet", 
                "Hasábkrumpli, husi, zöldségek, öntet", 
                "gyros tál dzsájrosz dzsajrosz tal",
                "[{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"onion\",\"_hide\":true,\"values\":[\"Lilahagyma\",\"Pirított hagyma\"],\"prices\":[0,0],\"aliases\":[\"LH\",\"PH\"]},{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"sauce\",\"_hide\":true,\"values\":[\"Sima\",\"Foghagymás\",\"Csípős\"],\"prices\":[0,0,0],\"aliases\":[\"SIMA\",\"FH\",\"CSÍP\"]},{\"type\":\"EXTRA_SELECT\",\"name\":\"cheese\",\"_hide\":true,\"values\":[\"Nem kérek\",\"Extra sajt\",\"Dupla sajt\"],\"prices\":[0,100,200],\"aliases\":[\"NE\",\"EXTRA x1\",\"Extra x2\"]},{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"extra\",\"_hide\":true,\"values\":[\"Extra hús\",\"Extra csípős\"],\"prices\":[200,50],\"aliases\":[\"HÚS\",\"CSÍP\"],\"_comment\":\"Extra csípős: mindenféle különleges esszenciát bevetve egyenesen a pokolból\"}]", 
                900, true, true, false, true, false, "cdn/items/5.jpg"));
        
        items.save(new ItemEntity("Gyros tortillában", circle, 
                "30 cm-es tortilla, husi, zöldségek, öntet", 
                "30 cm-es tortilla, husi, zöldségek, öntet", 
                "gyros trotilla dzsájrosz dzsajrosz",
                "[{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"onion\",\"_hide\":true,\"values\":[\"Lilahagyma\",\"Pirított hagyma\"],\"prices\":[0,0],\"aliases\":[\"LH\",\"PH\"]},{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"sauce\",\"_hide\":true,\"values\":[\"Sima\",\"Foghagymás\",\"Csípős\"],\"prices\":[0,0,0],\"aliases\":[\"SIMA\",\"FH\",\"CSÍP\"]},{\"type\":\"EXTRA_SELECT\",\"name\":\"cheese\",\"_hide\":true,\"values\":[\"Nem kérek\",\"Extra sajt\",\"Dupla sajt\"],\"prices\":[0,50,100],\"aliases\":[\"NE\",\"EXTRA x1\",\"Extra x2\"]},{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"extra\",\"_hide\":true,\"values\":[\"Extra hús\",\"Extra csípős\"],\"prices\":[200,50],\"aliases\":[\"HÚS\",\"CSÍP\"],\"_comment\":\"Extra csípős: mindenféle különleges esszenciát bevetve egyenesen a pokolból\"}]", 
                650, true, true, false, true, false, "cdn/items/5.jpg"));
        
        items.save(new ItemEntity("Gyros pitában", circle, 
                "pita, husi, zöldségek, öntet", 
                "Pita, husi, zöldségek, öntet", 
                "gyros pita dzsájrosz dzsajrosz",
                "[{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"onion\",\"_hide\":true,\"values\":[\"Lilahagyma\",\"Pirított hagyma\"],\"prices\":[0,0],\"aliases\":[\"LH\",\"PH\"]},{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"sauce\",\"_hide\":true,\"values\":[\"Sima\",\"Foghagymás\",\"Csípős\"],\"prices\":[0,0,0],\"aliases\":[\"SIMA\",\"FH\",\"CSÍP\"]},{\"type\":\"EXTRA_SELECT\",\"name\":\"cheese\",\"_hide\":true,\"values\":[\"Nem kérek\",\"Extra sajt\",\"Dupla sajt\"],\"prices\":[0,50,100],\"aliases\":[\"NE\",\"EXTRA x1\",\"Extra x2\"]},{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"extra\",\"_hide\":true,\"values\":[\"Extra hús\",\"Extra csípős\"],\"prices\":[200,50],\"aliases\":[\"HÚS\",\"CSÍP\"],\"_comment\":\"Extra csípős: mindenféle különleges esszenciát bevetve egyenesen a pokolból\"}]", 
                500, true, true, false, true, false, "cdn/items/5.jpg"));
        
        items.save(new ItemEntity("Gyros görög pitában", circle, 
                "20 cm-es görög pita, husi, zöldségek, sültkrumpli, öntet", 
                "20 cm-es görög pita, husi, zöldségek, sültkrumpli, öntet", 
                "gyros pita görög gorog dzsájrosz dzsajrosz",
                "[{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"onion\",\"_hide\":true,\"values\":[\"Lilahagyma\",\"Pirított hagyma\"],\"prices\":[0,0],\"aliases\":[\"LH\",\"PH\"]},{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"sauce\",\"_hide\":true,\"values\":[\"Sima\",\"Foghagymás\",\"Csípős\"],\"prices\":[0,0,0],\"aliases\":[\"SIMA\",\"FH\",\"CSÍP\"]},{\"type\":\"EXTRA_SELECT\",\"name\":\"cheese\",\"_hide\":true,\"values\":[\"Nem kérek\",\"Extra sajt\",\"Dupla sajt\"],\"prices\":[0,50,100],\"aliases\":[\"NE\",\"EXTRA x1\",\"Extra x2\"]},{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"extra\",\"_hide\":true,\"values\":[\"Extra hús\",\"Extra csípős\"],\"prices\":[200,50],\"aliases\":[\"HÚS\",\"CSÍP\"],\"_comment\":\"Extra csípős: mindenféle különleges esszenciát bevetve egyenesen a pokolból\"}]", 
                600, true, true, false, true, false, "cdn/items/5.jpg"));
        
        items.save(new ItemEntity("Gyros kifliben", circle, 
                "kifli, husi, zöldségek, öntet", 
                "Kifli, husi, zöldségek, öntet", 
                "gyros pita görög gorog dzsájrosz dzsajrosz",
                "[{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"onion\",\"_hide\":true,\"values\":[\"Lilahagyma\",\"Pirított hagyma\"],\"prices\":[0,0],\"aliases\":[\"LH\",\"PH\"]},{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"sauce\",\"_hide\":true,\"values\":[\"Sima\",\"Foghagymás\",\"Csípős\"],\"prices\":[0,0,0],\"aliases\":[\"SIMA\",\"FH\",\"CSÍP\"]},{\"type\":\"EXTRA_SELECT\",\"name\":\"cheese\",\"_hide\":true,\"values\":[\"Nem kérek\",\"Extra sajt\",\"Dupla sajt\"],\"prices\":[0,50,100],\"aliases\":[\"NE\",\"EXTRA x1\",\"Extra x2\"]},{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"extra\",\"_hide\":true,\"values\":[\"Extra hús\",\"Extra csípős\"],\"prices\":[200,50],\"aliases\":[\"HÚS\",\"CSÍP\"],\"_comment\":\"Extra csípős: mindenféle különleges esszenciát bevetve egyenesen a pokolból\"}]", 
                650, true, true, false, true, false, "cdn/items/5.jpg"));
                      
        
//        openings.save(opening = new OpeningEntity(convert(4, 18, 0), convert(4, 23, 0), convert(4, 0, 0), convert(4, 18, 0), 
//                "demo/dzsajrosz-pr.jpg", "Type your feeling here", circle, 2, 1, 0, 20));
        openings.save(opening = new OpeningEntity(convert(4, 18, 0), convert(4, 23, 0), fromNow(0), fromNow(10), 
                "demo/dzsajrosz-pr.jpg", "Ez egy Dzsájrosz nyitás. Ez a szöveg program sch-ra lesz exportálva.", 
                "Type your feeling here", circle, 2, 1, 0, 20));
        opening.generateTimeWindows(openings);
        openings.save(opening);
        
        circles.save(circle = new CircleEntity("Americano", 
                "A kört 2011 tavaszán alapították kék és fekete gólyák. A célunk az volt, hogy minőségi húsból "
                    + "készített hamburgert és amerikai hot-dogot készítsünk a kollégisták számára. A húspogácsát "
                    + "mi magunk készítjük marha- és disznóhúsból, minőségben messze lekörözi az utcán kapható "
                    + "gagyihúsos hamburgereket. Emellé jön a szintén saját speckó szószunk, melytől a hús még "
                    + "inkább elolvad a szádban.Hetente kedd esténként nyitunk a -1.-en a nagykonyhában. ", 
                "A kört 2011 tavaszán alapították kék és fekete gólyák. A célunk az volt, hogy minőségi húsból "
                    + "készített hamburgert és amerikai hot-dogot készítsünk a kollégisták számára. A húspogácsát "
                    + "mi magunk készítjük marha- és disznóhúsból, minőségben messze lekörözi az utcán kapható "
                    + "gagyihúsos hamburgereket. Emellé jön a szintén saját speckó szószunk, melytől a hús még "
                    + "inkább elolvad a szádban. Hetente kedd esténként nyitunk a -1.-en a nagykonyhában. ", 
                "blue",  2002,
                "demo/americano-bg.jpg", "icons/icon-americano.svg", "Kedd", "americano"));

        items.save(new ItemEntity("Random Burger", circle, 
                "Izé, hozé, bigyó és return 4, lorem ipsum dolor sit amet", 
                "Izé, hozé, bigyó és return 4", 
                "burger asd",
                "[{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"sauce\",\"values\":[\"Ketchup\",\"Majonéz\",\"Mustár\"],\"prices\":[0,50,150]}]", 
                600, true, true, false, true, false, "cdn/items/4.jpg"));
        
        openings.save(opening = new OpeningEntity(convert(2, 16, 0), convert(2, 20, 0), convert(2, 0, 0), convert(2, 14, 0), 
                "demo/americano-pr.jpg", "Ez egy Americano nyitás. Ez a szöveg program sch-ra lesz exportálva.", 
                 "Random moment cuccok", circle, 100, 20, 0, 10));
        opening.generateTimeWindows(openings);
        openings.save(opening);
        
        circles.save(circle = new CircleEntity("Vödör", 
                "A kör tagjai hétfő esténként krumplipucolóikkal és fritőzeikkel felszerelkezve nekiülnek és jó "
                    + "hangulatban megsütnek ~100 kg krumplit az éhes kollégisták nagy örömére. Akik szeretik a "
                    + "hazai, igazi „anyasütötte” krumpli ízét, azokat sok szeretettel várja hétfő esténként a Vödörkör.", 
                "A kör tagjai hétfő esténként krumplipucolóikkal és fritőzeikkel felszerelkezve nekiülnek és jó "
                    + "hangulatban megsütnek ~100 kg krumplit az éhes kollégisták nagy örömére. Akik szeretik a "
                    + "hazai, igazi „anyasütötte” krumpli ízét, azokat sok szeretettel várja hétfő esténként a Vödörkör.", 
                "purple",  2005,
                "demo/vodor-bg.jpg", "icons/icon-vodor.svg", "Hétfő", "vodor"));
        
        items.save(new ItemEntity("Vödör", circle, 
                "Izé, hozé, bigyó és 4, lorem ipsum dolor sit amet", 
                "Izé, hozé, bigyó és 4", 
                "sult krumpli",
                "[{\"type\":\"EXTRA_SELECT\",\"name\":\"potato\",\"_display\":\"1-6 {pieces}\",\"values\":[\"1 krumpli\",\"2 krumpli\",\"3 krumpli\",\"4 krumpli\",\"5 krumpli\",\"6 krumpli\"],\"prices\":[0,400,800,1200,1600,2000]}," + 
                "{\"type\":\"EXTRA_SELECT\",\"name\":\"panzo\",\"_display\":\"0-6 {pieces}\",\"values\":[\"Nem kérek\",\"1 panzo\",\"2 panzo\",\"3 panzo\",\"4 panzo\",\"5 panzo\",\"6 panzo\"],\"prices\":[0,200,400,600,800,1000,1200]}]", 
                600, true, true, false, true, false, "cdn/items/8.jpg"));
        
        openings.save(opening = new OpeningEntity(convert(1, 18, 0), convert(2, 0, 0), convert(1, 0, 0), convert(1, 18, 0), 
                "demo/dzsajrosz-pr.jpg", "Ez egy Vödör nyitás. Ez a szöveg program sch-ra lesz exportálva.", 
                 "Feeling típusú nyitás", circle, 100, 120, 0, 6 * 60));
        opening.generateTimeWindows(openings);
        openings.save(opening);
        
        circles.save(circle = new CircleEntity("Kakas", 
                "A Vörös Kakas Fogadó finom melegszendvicsekkel és alkoholos valamint alkoholmentes innivalóval "
                    + "várja a vendégeit szinte minden vasárnap este 9 órától egészen akár kora reggelig. "
                    + "Munkánkkal igyekszünk mindig jó hangulatú vasárnap estéket teremteni nem csak magunk, "
                    + "hanem vendégeink számára is. Jól mutatja ezt immár 23 éve tartó töretlen sikerességünk "
                    + "és a félévenkénti 10 feletti nyitásaink száma is.", 
                "A Vörös Kakas Fogadó finom melegszendvicsekkel és alkoholos valamint alkoholmentes innivalóval "
                    + "várja a vendégeit szinte minden vasárnap este 9 órától egészen akár kora reggelig. "
                    + "Munkánkkal igyekszünk mindig jó hangulatú vasárnap estéket teremteni nem csak magunk, "
                    + "hanem vendégeink számára is. Jól mutatja ezt immár 23 éve tartó töretlen sikerességünk "
                    + "és a félévenkénti 10 feletti nyitásaink száma is.", 
                "red", 1999,
                "demo/kakas-bg.jpg", "icons/icon-kakas.svg", "Vasárnap", "kakas"));
        
        openings.save(opening = new OpeningEntity(convert(7, 20, 0), convert(8, 0, 0), convert(7, 20, 0), convert(7, 20, 0), 
                "demo/kakas-pr.jpg", "Ez egy Kakas nyitás. Ez a szöveg program sch-ra lesz exportálva.", 
                 "Forradalmi nyitás", circle, 100, 20, 0, 0));
        opening.generateTimeWindows(openings);
        openings.save(opening);

        items.save(new ItemEntity("Sonkás melegszendvics", circle, 
                "Sonka, sajt, hagyma, vaj, lorem ipsum dolor sit amet", 
                "Sonka, sajt, hagyma, vaj", 
                "meleg szendvics sonka sajt hagyma",
                "[{\"name\":\"size\"}]", 
                200, true, true, false, true, true, "cdn/items/6.jpg"));
        
        circles.save(circle = new CircleEntity("Lángosch", 
                "A LángoSCH egy éve kezdte pályafutását a 18. emeleti konyhában. Azóta próbáljuk megidézni "
                    + "a strandok kellemes feelingjét kéthetente vasárnap a kollégiumban, eredeti házi recept "
                    + "alapján készült lángosainkkal. Habár az állandó kínálat már kialakult, a kísérletezéstől "
                    + "sem riadunk vissza és friss kör lévén bármilyen újdonságra vevők vagyunk. Klasszikus "
                    + "lángosok mellett a nutellás lángos bármely variációja megtalálható étlapunkon, egy alkalommal "
                    + "pedig extra bundáskenyeret is csináltunk már. Nyáron már közvetlenül a Balaton partján "
                    + "állítottuk fel sütödénket a villanykarosoknak. Itt a helyed, ha szeretnél tagja lenni egy "
                    + "frissen alakult körnek és egy mindenre nyitott csapatnak!", 
                "A LángoSCH egy éve kezdte pályafutását a 18. emeleti konyhában. Azóta próbáljuk megidézni "
                    + "a strandok kellemes feelingjét kéthetente vasárnap a kollégiumban, eredeti házi recept "
                    + "alapján készült lángosainkkal. Habár az állandó kínálat már kialakult, a kísérletezéstől "
                    + "sem riadunk vissza és friss kör lévén bármilyen újdonságra vevők vagyunk. Klasszikus "
                    + "lángosok mellett a nutellás lángos bármely variációja megtalálható étlapunkon, egy alkalommal "
                    + "pedig extra bundáskenyeret is csináltunk már. Nyáron már közvetlenül a Balaton partján "
                    + "állítottuk fel sütödénket a villanykarosoknak. Itt a helyed, ha szeretnél tagja lenni egy "
                    + "frissen alakult körnek és egy mindenre nyitott csapatnak!", 
                "yellow",  1994,
                "demo/langosch-bg.jpg", "icons/icon-langosch.svg", "Vasárnap (2 hetente)", "langosch"));
        
        openings.save(opening = new OpeningEntity(convert(7, 18, 0), convert(8, 0, 0), convert(7, 0, 0), convert(7, 18, 0), 
                "demo/langosch-pr.jpg", "Ez egy Lángosch nyitás. Ez a szöveg program sch-ra lesz exportálva.", 
                 "Tüzes lángos", circle, 100, 20, 0, 30));
        opening.generateTimeWindows(openings);
        openings.save(opening);

        items.save(new ItemEntity("Tüzes lángos", circle, 
                "Chili, sonka, paradicsom, mozzarella, sajtkrémes alap, lorem ipsum dolor sit amet", 
                "Chili, sonka, paradicsom, mozzarella, sajtkrémes alap", 
                "langsch langs tuzes chili",
                "[{\"name\":\"size\"}]", 
                500, true, true, false, true, false, "cdn/items/7.jpg"));
        
    }

    private long convert(int day, int hh, int mm) {
        final int month = 2;
        final int weekStart = 18 - 1;
        hh -= 1;
        if (hh < 0) {
            hh += 24;
            day -= 1;
        }
        return Instant.parse(String.format("2019-%02d-%02dT%02d:%02d:00Z", 
                month, weekStart + day, hh, mm)).toEpochMilli();
    }
    
    private long fromNow(int minutes) {
        return System.currentTimeMillis() + minutes * 60000;
    }
    
}
