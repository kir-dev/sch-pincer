package hu.kirdev.schpincer.config

import hu.kirdev.schpincer.model.CircleEntity
import hu.kirdev.schpincer.model.CircleMemberEntity
import hu.kirdev.schpincer.model.ItemEntity
import hu.kirdev.schpincer.model.OpeningEntity
import hu.kirdev.schpincer.service.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.Instant
import jakarta.annotation.PostConstruct

@Component
@Profile("test")
@SuppressWarnings("kotlin:S1192" // ignore duplicated string
)
class TestingConfig {

    @Autowired
    private lateinit var circles: CircleService

    @Autowired
    private lateinit var openings: OpeningService

    @Autowired
    private lateinit var items: ItemService

    @Autowired
    private lateinit var circleMembers: CircleMemberService

    @Autowired
    private lateinit var reviews: ReviewService

    @PostConstruct
    fun insertDbData() {
        var circle: CircleEntity
        circles.save(CircleEntity(
                "Pizzásch",
                "#h2#Hivatásunk#/h2#"
                        + "Szerda esténként az éhes kollégistákat szolgáljuk ki helybe sütött pizzáinkkal, amelyeket a "
                        + "FoodEX szállít a szobáikba. Emellett gyakran megfordulunk Gólyabálokon, bulikon, "
                        + "villanykaros és külsős rendezvényeken is. "
                        + "#h2#Történetünk#/h2#"
                        + "A PizzáSCH 2004 első felében jött létre egy baráti társaságból, és azóta látja el friss, "
                        + "helyben sütött pizzával az éhes kollégistákat."
                        + "#h2#Jelentkezés#/h2#"
                        + "Minden félév elején várjuk szeretettel a fiatalokat (idősebbeket), hogy csatlakozzanak hozzánk. "
                        + "Ehhez nincs más teendő, mint szerda délután megjelenni a -1. nagykonyhánál. "
                        + "További részleteket helyben elmondjuk.",
                "Szerda esténként az éhes kollégistákat szolgáljuk ki helybe sütött pizzáinkkal, amelyeket a "
                        + "FoodEX szállít a szobáikba. Emellett gyakran megfordulunk Gólyabálokon, bulikon, "
                        + "villanykaros és külsős rendezvényeken is.",
                "orange", 2004,
                "demo/pizzasch-bg.jpg", "icons/icon-pizzasch.svg", "Szerda", "pizzasch",
                "https://www.facebook.com/pizzasch/", "https://pizzasch.hu/pizzas", true).also { circle = it })
        var opening: OpeningEntity
        //        Original:
        openings.save(OpeningEntity(0, 30, fromNow(200), fromNow(260), fromNow(-30), fromNow(120),
                "demo/pizzasch-pr.jpg", "Ez egy Pizzásch nyitás. Ez a szöveg program sch-ra lesz exportálva.",
                "Jack pls mit írjak ide?", circle, mutableListOf(), 100, 20, 2, 30).also { opening = it })
        //        openings.save(opening = new OpeningEntity(convert(3, 18, 0), convert(4, 0, 0), fromNow(0), fromNow(100),
//                "demo/pizzasch-pr.jpg", "Ez egy Pizzásch nyitás. Ez a szöveg program sch-ra lesz exportálva.",
//                "Jack pls mit írjak ide?", circle, 100, 20, 2, 30));
        opening.generateTimeWindows(openings)
        openings.save(opening)
        circleMembers.save(CircleMemberEntity(0, circle, "Valami Dezső", "Körvez", "demo/profile-pic-1.jpg", 10))
        circleMembers.save(CircleMemberEntity(0, circle, "Test Elek", "Tag", "demo/profile-pic-1.jpg", 0))
        circleMembers.save(CircleMemberEntity(0, circle, "Lány Név", "Gazdaságis", "demo/profile-pic-2.jpg", 5))
        circleMembers.save(CircleMemberEntity(0, circle, "Valami János", "Tag", "demo/profile-pic-1.jpg", 0))
        circleMembers.save(CircleMemberEntity(0, circle, "Valami Péter", "Tag", "demo/profile-pic-1.jpg", 0))
        circleMembers.save(CircleMemberEntity(0, circle, "Valami Bogi", "Tag", "demo/profile-pic-2.jpg", 0))
        circleMembers.save(CircleMemberEntity(0, circle, "Valami Sándor", "Tag", "demo/profile-pic-1.jpg", 0))
        circleMembers.save(CircleMemberEntity(0, circle, "Valami József", "Tag", "demo/profile-pic-1.jpg", 0))
        circleMembers.save(CircleMemberEntity(0, circle, "Valami Attila", "Tag", "demo/profile-pic-1.jpg", 0))
        //        reviews.save(new ReviewEntity(circle, "Szabó Gergely", LONG_LOREM_IPSUM,
//                Instant.now().toEpochMilli(), 5, 3, 5, 5));
//        reviews.save(new ReviewEntity(circle, "Kredit Huszár", "Csicskagyász ez a kör",
//                Instant.now().toEpochMilli() - 400000000, 1, 1, 1, 1));
//        reviews.save(new ReviewEntity(circle, "Tavasz Gábor",
//                "Tesz szöveg. Teszt értékelés. A kockázatok és a mellékhatások pls...",
//                Instant.now().toEpochMilli() + 332112300, 4, 5, 4, 4));
        items.save(ItemEntity(0, "Albínó Batman", circle,
                "Fokhagymás alap, Pick szalámi, Lilahagyma, Kukorica, Mozzarella",
                "Fokhagymás alap, Pick szalámi, Lilahagyma, Kukorica, Mozzarella",
                " pizza pizzasch albino batman",
                "[{\"type\":\"PIZZASCH_SELECT\",\"name\":\"size\",\"values\":[\"32\"],\"aliases\":[\"normál\"],\"prices\":[0]},{\"type\":\"ITEM_COUNT\",\"name\":\"count\",\"min\":1,\"max\":10,\"values\":[],\"_hide\":true},{\"type\":\"AB_KB_SELECT\",\"name\":\"kb_extra\",\"values\":[\"Nem kérek\",\"+ Gomba\",\"+ Kukorica\",\"+ Ananász\"],\"aliases\":[\"-\",\"GOMBA\",\"KUKOR\",\"ANANS\"],\"prices\":[0,0,0,0],\"_hide\":true},{\"type\":\"AB_SELECT\",\"name\":\"ab_extra\",\"values\":[\"Nem kérek\",\"+ Gomba\",\"+ Kukorica\",\"+ Ananász\"],\"aliases\":[\"-\",\"GOMBA\",\"KUKOR\",\"ANANS\"],\"prices\":[0,0,0,0],\"_hide\":true}]",
                800, true, true, false, true, false, false, "cdn/items/5.jpg", 0))
        items.save(ItemEntity(0, "BBQ", circle,
                "BBQ alap, Bacon, Sonka, Lilahagyma, Mozzarella",
                "BBQ alap, Bacon, Sonka, Lilahagyma, Mozzarella",
                " pizza pizzasch bbq",
                "[{\"type\":\"PIZZASCH_SELECT\",\"name\":\"size\",\"values\":[\"32\",\"45\"],\"aliases\":[\"kis\",\"nagy\"],\"prices\":[0,200],\"_comment\":\"A 45-ös pizzából csak limitált mennyiségűt készítünk.\"}]",
                800, true, true, false, true, false, false, "cdn/items/bbq.jpeg", 0))
        items.save(ItemEntity(0, "HahaA", circle,
                "Paradicsomos alap, Tonhal, Olívabogyó, Vöröshagyma, Mozzarella",
                "Paradicsomos alap, Tonhal, Olívabogyó, Vöröshagyma, Mozzarella",
                " pizza pizzasch hahaa",
                "[{\"type\":\"PIZZASCH_SELECT\",\"name\":\"size\",\"values\":[\"32\",\"45\"],\"aliases\":[\"kis\",\"nagy\"],\"prices\":[0,200],\"_comment\":\"A 45-ös pizzából csak limitált mennyiségűt készítünk.\"}]",
                800, true, true, false, true, false, false, "cdn/items/hahaa.jpeg", 0))
        items.save(ItemEntity(0, "Hawaii", circle,
                "Paradicsomos alap, Sonka, Kukorica, Ananász, Mozzarella",
                "Paradicsomos alap, Sonka, Kukorica, Ananász, Mozzarella",
                " pizza pizzasch hawaii",
                "[{\"type\":\"PIZZASCH_SELECT\",\"name\":\"size\",\"values\":[\"32\",\"45\"],\"aliases\":[\"kis\",\"nagy\"],\"prices\":[0,200],\"_comment\":\"A 45-ös pizzából csak limitált mennyiségűt készítünk.\"}]",
                800, true, true, false, true, false, false, "cdn/items/hawaii.jpeg", 0))
        items.save(ItemEntity(0, "Joker", circle,
                "Paradicsomos alap, Pick szalámi, Bacon, Lilahagyma, Mozzarella",
                "Paradicsomos alap, Pick szalámi, Bacon, Lilahagyma, Mozzarella",
                " pizza pizzasch joker",
                "[{\"type\":\"PIZZASCH_SELECT\",\"name\":\"size\",\"values\":[\"32\"],\"aliases\":[\"normál\"],\"prices\":[0]},{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"extra\",\"values\":[\"6db Grissini\",\"Fokhagymás szósz\",\"Paradicsomos szósz\"],\"prices\":[200,100,100],\"aliases\":[\"griss\",\"sz:fokh\",\"sz:para\"],\"_hide\":true},{\"type\":\"ITEM_COUNT\",\"name\":\"count\",\"min\":1,\"max\":10,\"values\":[],\"_hide\":true},{\"type\":\"AB_KB_SELECT\",\"name\":\"kb_extra\",\"values\":[\"Nem kérek\",\"+ Gomba\",\"+ Kukorica\",\"+ Ananász\"],\"aliases\":[\"-\",\"GOMBA\",\"KUKOR\",\"ANANS\"],\"prices\":[0,0,0,0],\"_hide\":true},{\"type\":\"AB_SELECT\",\"name\":\"ab_extra\",\"values\":[\"Nem kérek\",\"+ Gomba\",\"+ Kukorica\",\"+ Ananász\"],\"aliases\":[\"-\",\"GOMBA\",\"KUKOR\",\"ANANS\"],\"prices\":[0,0,0,0],\"_hide\":true}]",
                1000, true, true, false, true, true, false, "cdn/items/joker.jpeg", 0))
        items.save(ItemEntity(0, "Kusza", circle,
                "Paradicsomos alap, Pick szalámi, Kukorica, Mozzarella",
                "Paradicsomos alap, Pick szalámi, Kukorica, Mozzarella",
                " pizza pizzasch kusza",
                "[{\"type\":\"PIZZASCH_SELECT\",\"name\":\"size\",\"values\":[\"32\",\"45\"],\"aliases\":[\"kis\",\"nagy\"],\"prices\":[0,200],\"_comment\":\"A 45-ös pizzából csak limitált mennyiségűt készítünk.\"}]",
                800, true, true, false, true, false, false, "cdn/items/kusza.jpeg", 8))
        items.save(ItemEntity(0, "Magyaros", circle,
                "Paradicsomos alap, Bacon, Pick szalámi, Vöröshagyma, Erős paprika, Mozzarella",
                "Paradicsomos alap, Bacon, Pick szalámi, Vöröshagyma, Erős paprika, Mozzarella",
                " pizza pizzasch magyaros",
                "[{\"type\":\"PIZZASCH_SELECT\",\"name\":\"size\",\"values\":[\"32\",\"45\"],\"aliases\":[\"kis\",\"nagy\"],\"prices\":[0,200],\"_comment\":\"A 45-ös pizzából csak limitált mennyiségűt készítünk.\"}]",
                800, true, true, false, true, false, false, "cdn/items/pizzasch_blank.jpeg", 0))
        items.save(ItemEntity(0, "Margherita", circle,
                "Paradicsomos alap, Paradicsom, Mozzarella",
                "Paradicsomos alap, Paradicsom, Mozzarella",
                " pizza pizzasch margherita",
                "[{\"type\":\"PIZZASCH_SELECT\",\"name\":\"size\",\"values\":[\"32\",\"45\"],\"aliases\":[\"kis\",\"nagy\"],\"prices\":[0,200],\"_comment\":\"A 45-ös pizzából csak limitált mennyiségűt készítünk.\"}]",
                800, true, true, false, true, false, false, "cdn/items/pizzasch_blank.jpeg", 0))
        items.save(ItemEntity(0, "McStar", circle,
                "Mustáros alap, Sonka, Bacon, Lilahagyma, Mozzarella",
                "Mustáros alap, Sonka, Bacon, Lilahagyma, Mozzarella",
                " pizza pizzasch mcstar",
                "[{\"type\":\"PIZZASCH_SELECT\",\"name\":\"size\",\"values\":[\"32\",\"45\"],\"aliases\":[\"kis\",\"nagy\"],\"prices\":[0,200],\"_comment\":\"A 45-ös pizzából csak limitált mennyiségűt készítünk.\"}]",
                800, true, true, false, true, false, false, "cdn/items/mcstar.jpeg", 0))
        items.save(ItemEntity(0, "Ördög", circle,
                "Erős alap, Pick szalámi, Bacon, Pepperóni, Mozzarella",
                "Erős alap, Pick szalámi, Bacon, Pepperóni, Mozzarella",
                " pizza pizzasch ördög ordog",
                "[{\"type\":\"PIZZASCH_SELECT\",\"name\":\"size\",\"values\":[\"32\",\"45\"],\"aliases\":[\"kis\",\"nagy\"],\"prices\":[0,200],\"_comment\":\"A 45-ös pizzából csak limitált mennyiségűt készítünk.\"}]",
                800, true, true, false, true, false, false, "cdn/items/pizzasch_blank.jpeg", 0))
        items.save(ItemEntity(0, "Randi", circle,
                "Fokhagymás alap, Bacon, Sonka, Vöröshagyma, Mozzarella",
                "Fokhagymás alap, Bacon, Sonka, Vöröshagyma, Mozzarella",
                " pizza pizzasch randi",
                "[{\"type\":\"PIZZASCH_SELECT\",\"name\":\"size\",\"values\":[\"32\",\"45\"],\"aliases\":[\"kis\",\"nagy\"],\"prices\":[0,200],\"_comment\":\"A 45-ös pizzából csak limitált mennyiségűt készítünk.\"}]",
                800, true, true, false, true, false, false, "cdn/items/randi.jpeg", 0))
        items.save(ItemEntity(0, "Songoku", circle,
                "Paradicsomos alap, Sonka, Kukorica, Gomba, Mozzarella",
                "Paradicsomos alap, Sonka, Kukorica, Gomba, Mozzarella",
                " pizza pizzasch songoku",
                "[{\"type\":\"PIZZASCH_SELECT\",\"name\":\"size\",\"values\":[\"32\",\"45\"],\"aliases\":[\"kis\",\"nagy\"],\"prices\":[0,200],\"_comment\":\"A 45-ös pizzából csak limitált mennyiségűt készítünk.\"}]",
                800, true, true, false, true, false, false, "cdn/items/songoku.jpeg", 0))
        items.save(ItemEntity(0, "Sonkás", circle,
                "Paradicsomos alap, Sonka, Paradicsom, Mozzarella",
                "Paradicsomos alap, Sonka, Paradicsom, Mozzarella",
                " pizza pizzasch sonkas sonkás",
                "[{\"type\":\"PIZZASCH_SELECT\",\"name\":\"size\",\"values\":[\"32\",\"45\"],\"aliases\":[\"kis\",\"nagy\"],\"prices\":[0,200],\"_comment\":\"A 45-ös pizzából csak limitált mennyiségűt készítünk.\"}]",
                800, true, true, false, true, false, false, "cdn/items/sonkas.jpeg", 0))
        items.save(ItemEntity(0, "Szalámis", circle,
                "Paradicsomos alap, Pick szalámi, Paprika, Paradicsom, Mozzarella",
                "Paradicsomos alap, Pick szalámi, Paprika, Paradicsom, Mozzarella",
                " pizza pizzasch szalamis szalámis",
                "[{\"type\":\"PIZZASCH_SELECT\",\"name\":\"size\",\"values\":[\"32\",\"45\"],\"aliases\":[\"kis\",\"nagy\"],\"prices\":[0,200],\"_comment\":\"A 45-ös pizzából csak limitált mennyiségűt készítünk.\"}]",
                800, true, true, false, true, false, false, "cdn/items/szalamis.jpeg", 0))
        items.save(ItemEntity(0, "Yolo", circle,
                "Fokhagymás alap, Sonka, Paradicsom, Olívabogyó, Mozzarella",
                "Fokhagymás alap, Sonka, Paradicsom, Olívabogyó, Mozzarella",
                " pizza pizzasch yolo",
                "[{\"type\":\"PIZZASCH_SELECT\",\"name\":\"size\",\"values\":[\"32\",\"45\"],\"aliases\":[\"kis\",\"nagy\"],\"prices\":[0,200],\"_comment\":\"A 45-ös pizzából csak limitált mennyiségűt készítünk.\"}]",
                800, true, true, false, true, false, false, "cdn/items/7.jpg", 68))
        circles.save(CircleEntity("Dzsájrosz",
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
                "demo/dzsajrosz-bg.jpg", "icons/icon-dzsajrosz.svg", "Csütörtök", "dzsajrosz",
                "https://www.facebook.com/dzsajrosz/", "http://dzsajrosz.sch.bme.hu/", true).also({ circle = it }))
        items.save(ItemEntity(0, "Normál gyros tál", circle,
                "hasábkrumpli, husi, zöldségek, öntet",
                "Hasábkrumpli, husi, zöldségek, öntet",
                "gyros tál dzsájrosz dzsajrosz tal",
                "[{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"onion\",\"_hide\":true,\"values\":[\"Lilahagyma\",\"Pirított hagyma\"],\"prices\":[0,0],\"aliases\":[\"LH\",\"PH\"]},{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"sauce\",\"_hide\":true,\"values\":[\"Sima\",\"Foghagymás\",\"Csípős\"],\"prices\":[0,0,0],\"aliases\":[\"SIMA\",\"FH\",\"CSÍP\"]},{\"type\":\"EXTRA_SELECT\",\"name\":\"cheese\",\"_hide\":true,\"values\":[\"Nem kérek\",\"Extra sajt\",\"Dupla sajt\"],\"prices\":[0,100,200],\"aliases\":[\"NE\",\"EXTRA x1\",\"Extra x2\"]},{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"extra\",\"_hide\":true,\"values\":[\"Extra hús\",\"Extra csípős\"],\"prices\":[200,50],\"aliases\":[\"HÚS\",\"CSÍP\"],\"_comment\":\"Extra csípős: mindenféle különleges esszenciát bevetve egyenesen a pokolból\"}]",
                900, true, true, false, true, false, false, "cdn/items/tal.jpg", 0))
        items.save(ItemEntity(0, "Gyros tortillában", circle,
                "30 cm-es tortilla, husi, zöldségek, öntet",
                "30 cm-es tortilla, husi, zöldségek, öntet",
                "gyros trotilla dzsájrosz dzsajrosz",
                "[{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"onion\",\"_hide\":true,\"values\":[\"Lilahagyma\",\"Pirított hagyma\"],\"prices\":[0,0],\"aliases\":[\"LH\",\"PH\"]},{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"sauce\",\"_hide\":true,\"values\":[\"Sima\",\"Foghagymás\",\"Csípős\"],\"prices\":[0,0,0],\"aliases\":[\"SIMA\",\"FH\",\"CSÍP\"]},{\"type\":\"EXTRA_SELECT\",\"name\":\"cheese\",\"_hide\":true,\"values\":[\"Nem kérek\",\"Extra sajt\",\"Dupla sajt\"],\"prices\":[0,50,100],\"aliases\":[\"NE\",\"EXTRA x1\",\"Extra x2\"]},{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"extra\",\"_hide\":true,\"values\":[\"Extra hús\",\"Extra csípős\"],\"prices\":[200,50],\"aliases\":[\"HÚS\",\"CSÍP\"],\"_comment\":\"Extra csípős: mindenféle különleges esszenciát bevetve egyenesen a pokolból\"}]",
                650, true, true, false, true, false, false, "cdn/items/tortilla.jpg", 0))
        items.save(ItemEntity(0, "Gyros pitában", circle,
                "pita, husi, zöldségek, öntet",
                "Pita, husi, zöldségek, öntet",
                "gyros pita dzsájrosz dzsajrosz",
                "[{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"onion\",\"_hide\":true,\"values\":[\"Lilahagyma\",\"Pirított hagyma\"],\"prices\":[0,0],\"aliases\":[\"LH\",\"PH\"]},{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"sauce\",\"_hide\":true,\"values\":[\"Sima\",\"Foghagymás\",\"Csípős\"],\"prices\":[0,0,0],\"aliases\":[\"SIMA\",\"FH\",\"CSÍP\"]},{\"type\":\"EXTRA_SELECT\",\"name\":\"cheese\",\"_hide\":true,\"values\":[\"Nem kérek\",\"Extra sajt\",\"Dupla sajt\"],\"prices\":[0,50,100],\"aliases\":[\"NE\",\"EXTRA x1\",\"Extra x2\"]},{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"extra\",\"_hide\":true,\"values\":[\"Extra hús\",\"Extra csípős\"],\"prices\":[200,50],\"aliases\":[\"HÚS\",\"CSÍP\"],\"_comment\":\"Extra csípős: mindenféle különleges esszenciát bevetve egyenesen a pokolból\"}]",
                500, true, true, false, true, false, false, "cdn/items/pita.jpg", 0))
        items.save(ItemEntity(0, "Gyros görög pitában", circle,
                "20 cm-es görög pita, husi, zöldségek, sültkrumpli, öntet",
                "20 cm-es görög pita, husi, zöldségek, sültkrumpli, öntet",
                "gyros pita görög gorog dzsájrosz dzsajrosz",
                "[{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"onion\",\"_hide\":true,\"values\":[\"Lilahagyma\",\"Pirított hagyma\"],\"prices\":[0,0],\"aliases\":[\"LH\",\"PH\"]},{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"sauce\",\"_hide\":true,\"values\":[\"Sima\",\"Foghagymás\",\"Csípős\"],\"prices\":[0,0,0],\"aliases\":[\"SIMA\",\"FH\",\"CSÍP\"]},{\"type\":\"EXTRA_SELECT\",\"name\":\"cheese\",\"_hide\":true,\"values\":[\"Nem kérek\",\"Extra sajt\",\"Dupla sajt\"],\"prices\":[0,50,100],\"aliases\":[\"NE\",\"EXTRA x1\",\"Extra x2\"]},{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"extra\",\"_hide\":true,\"values\":[\"Extra hús\",\"Extra csípős\"],\"prices\":[200,50],\"aliases\":[\"HÚS\",\"CSÍP\"],\"_comment\":\"Extra csípős: mindenféle különleges esszenciát bevetve egyenesen a pokolból\"}]",
                600, true, true, false, true, false, false, "cdn/items/gorog_pita.jpg", 0))
        items.save(ItemEntity(0, "Gyros kifliben", circle,
                "kifli, husi, zöldségek, öntet",
                "Kifli, husi, zöldségek, öntet",
                "gyros pita görög gorog dzsájrosz dzsajrosz",
                "[{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"onion\",\"_hide\":true,\"values\":[\"Lilahagyma\",\"Pirított hagyma\"],\"prices\":[0,0],\"aliases\":[\"LH\",\"PH\"]},{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"sauce\",\"_hide\":true,\"values\":[\"Sima\",\"Foghagymás\",\"Csípős\"],\"prices\":[0,0,0],\"aliases\":[\"SIMA\",\"FH\",\"CSÍP\"]},{\"type\":\"EXTRA_SELECT\",\"name\":\"cheese\",\"_hide\":true,\"values\":[\"Nem kérek\",\"Extra sajt\",\"Dupla sajt\"],\"prices\":[0,50,100],\"aliases\":[\"NE\",\"EXTRA x1\",\"Extra x2\"]},{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"extra\",\"_hide\":true,\"values\":[\"Extra hús\",\"Extra csípős\"],\"prices\":[200,50],\"aliases\":[\"HÚS\",\"CSÍP\"],\"_comment\":\"Extra csípős: mindenféle különleges esszenciát bevetve egyenesen a pokolból\"}]",
                650, true, false, false, true, false, false, "cdn/items/5.jpg", 0))
        openings.save(OpeningEntity(0, 30, convert(4, 18, 0), convert(4, 23, 0), convert(4, 0, 0), convert(4, 18, 0),  //                "demo/dzsajrosz-pr.jpg", "Type your feeling here", circle, 2, 1, 0, 20));
//        openings.save(opening = new OpeningEntity(convert(4, 18, 0), convert(4, 23, 0), fromNow(0), fromNow(10),
                "demo/dzsajrosz-pr.jpg", "Ez egy Dzsájrosz nyitás. Ez a szöveg program sch-ra lesz exportálva.",
                "Type your feeling here", circle, mutableListOf(), 2, 1, 0, 20).also { opening = it })
        opening.generateTimeWindows(openings)
        openings.save(opening)
        circles.save(CircleEntity("Americano",
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
                "blue", 2002,
                "demo/americano-bg.jpg", "icons/icon-americano.svg", "Kedd", "americano",
                "https://www.facebook.com/americano.sch/", "https://americano.sch.bme.hu/", true).also({ circle = it }))
        items.save(ItemEntity(0, "Random Burger", circle,
                "Izé, hozé, bigyó és return 4, lorem ipsum dolor sit amet",
                "Izé, hozé, bigyó és return 4",
                "burger asd",
                "[{\"type\":\"EXTRA_CHECKBOX\",\"name\":\"extra\",\"_hide\":false,\"values\":[\"Csípős szósz\",\"Jalapeno paprika\",\"Tükörtojás\",\"Pirított hagyma\"],\"prices\":[50,50,50,0],\"aliases\":[\"CSIP\",\"JAL\",\"TOJ\",\"PH\"]},{\"type\":\"ITEM_COUNT\",\"name\":\"count\",\"min\":1,\"max\":10,\"values\":[],\"_hide\":true}]",
                600, true, true, false, true, false, false, "cdn/items/4.jpg", 0))
        openings.save(OpeningEntity(0, 30, convert(2, 16, 0), convert(2, 20, 0), convert(2, 0, 0), convert(2, 14, 0),
                "demo/americano-pr.jpg", "Ez egy Americano nyitás. Ez a szöveg program sch-ra lesz exportálva.",
                "Random moment cuccok", circle, mutableListOf(), 100, 20, 0, 10).also { opening = it })
        opening.generateTimeWindows(openings)
        openings.save(opening)
        circles.save(CircleEntity("Vödör",
                "A kör tagjai hétfő esténként krumplipucolóikkal és fritőzeikkel felszerelkezve nekiülnek és jó "
                        + "hangulatban megsütnek ~100 kg krumplit az éhes kollégisták nagy örömére. Akik szeretik a "
                        + "hazai, igazi „anyasütötte” krumpli ízét, azokat sok szeretettel várja hétfő esténként a Vödörkör.",
                "A kör tagjai hétfő esténként krumplipucolóikkal és fritőzeikkel felszerelkezve nekiülnek és jó "
                        + "hangulatban megsütnek ~100 kg krumplit az éhes kollégisták nagy örömére. Akik szeretik a "
                        + "hazai, igazi „anyasütötte” krumpli ízét, azokat sok szeretettel várja hétfő esténként a Vödörkör.",
                "purple", 2005,
                "demo/vodor-bg.jpg", "icons/icon-vodor.svg", "Hétfő", "vodor",
                "https://www.facebook.com/vodorkor/", "https://vodor.sch.bme.hu/", true).also { circle = it })
        items.save(ItemEntity(0, "Vödör", circle,
                "A panzó elnevezés az olasz panzerotti szóból ered. Jelentése: töltött lángos. A mi receptünk alapján a lángos tésztába mexikói zöldségkeveréket és darálthúst teszünk, amit ezután forró olajban kisütünk.",
                "Lángos tészta, mexikói zöldségkeverék, darálthús és sűltkrumpli",
                "sult krumpli panzo",
                "[{\"type\":\"EXTRA_SELECT\",\"name\":\"potato\",\"_display\":\"1-6 {pieces}\",\"values\":[\"1 krumpli\",\"2 krumpli\",\"3 krumpli\",\"4 krumpli\",\"5 krumpli\",\"6 krumpli\"],\"prices\":[0,400,800,1200,1600,2000]}," +
                        "{\"type\":\"EXTRA_SELECT\",\"name\":\"panzo\",\"_display\":\"0-6 {pieces}\",\"values\":[\"Nem kérek\",\"1 panzo\",\"2 panzo\",\"3 panzo\",\"4 panzo\",\"5 panzo\",\"6 panzo\"],\"prices\":[0,200,400,600,800,1000,1200]}]",
                600, true, true, false, true, false, false, "cdn/items/panzo.jpg", 0))
        openings.save(OpeningEntity(0, 30, convert(1, 18, 0), convert(2, 0, 0), convert(1, 0, 0), convert(1, 18, 0),
                "demo/dzsajrosz-pr.jpg", "Ez egy Vödör nyitás. Ez a szöveg program sch-ra lesz exportálva.",
                "Feeling típusú nyitás", circle, mutableListOf(), 100, 120, 0, 6 * 60).also { opening = it })
        opening.generateTimeWindows(openings)
        openings.save(opening)
        circles.save(CircleEntity("Kakas",
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
                "demo/kakas-bg.jpg", "icons/icon-kakas.svg", "Vasárnap", "kakas",
                "https://www.facebook.com/kakasfogado/", "http://kakas.sch.bme.hu/", true).also({ circle = it }))
        openings.save(OpeningEntity(0, 30, convert(7, 20, 0), convert(8, 0, 0), convert(7, 20, 0), convert(7, 20, 0),
                "demo/kakas-pr.jpg", "Ez egy Kakas nyitás. Ez a szöveg program sch-ra lesz exportálva.",
                "Forradalmi nyitás", circle, mutableListOf(), 100, 20, 0, 0).also { opening = it })
        opening.generateTimeWindows(openings)
        openings.save(opening)
        items.save(ItemEntity(0, "Sonkás melegszendvics", circle,
                "Sonka, sajt, hagyma, vaj, lorem ipsum dolor sit amet",
                "Sonka, sajt, hagyma, vaj",
                "meleg szendvics sonka sajt hagyma",
                "[{\"name\":\"size\"}]",
                200, true, true, false, true, true, false, "cdn/items/6.jpg", 0))
        items.save(ItemEntity(0, "Protected melegszendvics", circle,
                "Sonka, sajt, hagyma, vaj, lorem ipsum dolor sit amet",
                "Sonka, sajt, hagyma, vaj",
                "meleg szendvics sonka sajt hagyma",
                "[{\"name\":\"size\"}]",
                200, true, true, false, false, true, false, "cdn/items/6.jpg", 0))
        circles.save(CircleEntity("Lángosch",
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
                "yellow", 1994,
                "demo/langosch-bg.jpg", "icons/icon-langosch.svg", "Vasárnap (2 hetente)", "langosch",
                "https://www.facebook.com/EgyelLangost/", "https://langosch.sch.bme.hu", true).also({ circle = it }))
        openings.save(OpeningEntity(0, 30, convert(7, 18, 0), convert(8, 0, 0), convert(7, 0, 0), convert(7, 18, 0),
                "demo/langosch-pr.jpg", "Ez egy Lángosch nyitás. Ez a szöveg program sch-ra lesz exportálva.",
                "Tüzes lángos", circle, mutableListOf(), 100, 20, 0, 30).also { opening = it })
        opening.generateTimeWindows(openings)
        openings.save(opening)
        items.save(ItemEntity(0, "Tüzes lángos", circle,
                "Chili, sonka, paradicsom, mozzarella, sajtkrémes alap, lorem ipsum dolor sit amet",
                "Chili, sonka, paradicsom, mozzarella, sajtkrémes alap",
                "langosch langos tuzes chili",
                "[{\"name\":\"size\"}]",
                500, true, true, false, true, false, false, "cdn/items/7.jpg", 0))
    }

    private fun convert(days: Int, hours: Int, mm: Int): Long {
        var day = days
        var hh = hours
        val month = 5
        val weekStart = 20 - 1
        hh -= 1
        if (hh < 0) {
            hh += 24
            day -= 1
        }
        return Instant.parse(String.format("2021-%02d-%02dT%02d:%02d:00Z",
                month, weekStart + day, hh, mm)).toEpochMilli()
    }

    private fun fromNow(minutes: Int): Long {
        return Instant.now().toEpochMilli() + minutes * 60000
    }

}
