SCH-PINCÉR Ultimate Guide köröknek
===

|Adat               |Érték                   |
|:------------------|:-----------------------|
|Utolsó módosítás   |2019.11.11.             |
|Aktuális fejlesztő |Szabó Gergely (Schámán) |
|Schpincér verzió   |1.1.7+                  |

# 1. Csatlakozás/Testreszabás

> A megadott képméretek csak tájékoztató jellegűek, ilyen méretben fognak megjelenni. (WxH formátumban)

### 1.0 Első lépések

- Körvezetőként kérj jogot a köröd szerkesztéséhez (tervben van auto export)
- Körvezetőként add le az aktuális jogosult tagokat. Körvezető jogkört kaphat bárki a körből, ahogyan PR-t is, részletek lejjebb. A jogkör megnevezése külsőleg nem látszik, szóval akár mindenkinek is lehet kérni ilyen jogkört.

### 1.1 Mire lesz szüksége a körnek

- 80x80-as monokróm (fehér és átlátszó) logó, PNG és SVG ajánlott
- 600x300-as kép minden termékről
- Termék adatai (ár, leírás, hozzávalók, választható feltétek, stb...) lásd lentebb
- Nyitás kép (1200x500 PR kép, ami a fő oldalon fog megjelenni), nyitás feelingje (opcionális)

### 1.2 Opcionális testreszabás, flagek

- Tagok felsorolása, van lehetőség fotót is felrakni fotóval (100x100)
- Flag-ek: Termék képe fölött megjelenő badge. (pl: A hét pizzája, ajándék pita) Ha ilyet szeretne valamelyik kör, akkor azt előző hét csütörtökig tegye meg lehetőleg, mert új flag hozzáadásához redeploy kell. (Ha már megvan a flag korlátlanul ki/be kapcsolható) 

|Flag neve                |Ikon             |Kód |
|-------------------------|-----------------|:--:|
|Semmi                    |Nincs            |0   |
|A hét pizzája            |A7'P'JA badge    |8   |
|10% akció                |-10%             |23  |
|20% akció                |-20%             |22  |
|25% akció                |-25%             |21  |
|50% akció                |-50%             |20  |
|69% akció                |NICE             |69  |
|Teszt termék             |Teszt és kémcső  |100 |
|-                        |-                |-   |
|Editors` choice*         |Trófea           |1010|

> \* 1000 feletti kódokat csak admin jogkörrel lehet beállítani

### 1.3 Export formátum

- Az export formátumot a mindenkori fejlesztővel egyeztetve pontosítsa minden kör, még az első nyitás előtt.
- Van lehetőség webhookos összeköttetésre is. Ha ilyet szeretne valaki, akkor ugyanúgy a mindenkori fejlesztőt keresse meg.

# 2. Jogkörök

### PR (Korlátozott jogkör)

- Nyitás hozzáadása
- Rendelések exportálása
- Termékek szerkesztése/hozzáadása/törlése

### Körvezető (Teljes jogkör)

- PR jogkörből minden
- Kör adatlapjának szerkesztése
- Tagok kezelése

### Admin (Site-szintű teljes jogkör)

- *Mindenkori fejlesztők és reszortvez részére*
- Kör hozzáadása, törlése
- Felhasználók jogköreinek szerkesztése

# 3. Nyitás készítése

1. Körök szerkesztése menü (bejelentkezés után látszik)
2. Kör kiválasztása
3. Új nyitás gomb
4. Nyitás kezdete/vége: Az az időszak amin belül a kiszállítás történik.
5. Rendelés kezdete/vége: Az a tartomány amin belül lehet rendelni. Körönként egy időben csak egy rendelési sáv legyen aktív.
6. A PR kép mérete optimálisan: 1200x500
7. A többi beállítás intuitív, hozzáadás gomb
8. **A nyitás nem szerkeszthető jelenleg. Ha valami el lett rontva, töröld le és hozd létre újra.**

# 4. Termék felvétele/Testreszabása

1. Körök szerkesztése menü (bejelentkezés után látszik)
2. Kör kiválasztása
3. Új termék / Ceruza ikon egy terméken
4. Leírás: Az a szöveg ami a felugró ablakban jelenik meg.
5. Hozzávalók: Az a szöveg ami a termékek listában jelenik meg. Formátum: Alapanyagok neveinek felsorolása vesszővel elválasztva.
6. Kulcsszavak: felsorolás szóközzel elválasztva
7. Beállítások JSON (lásd 5. pont)
8. Flag (lásd 1.2 pont)
9. Módosít / hozzáad gomb

> A kulcsszavak elterjedésének lehet egy kis ideje a lucene full text index miatt. 

# 5. Beállítások JSON (Advanced)

> A termékek menüjének beállításait tartalmazó JSON

### 5.1 Felépítése

A végleges json egy objektum lista: `[{...}, {...}]` Az egyes objektumok a beviteli mezők egy-egy csoportja, azaz egy komponens.

Ha nem világos, keresd meg az aktuális fejlesztőt nyugodtan.

### 5.2 Komponens formátum

``` json
{
    "type": "EXTRA_SELECT",          // Can be: EXTRA_SELECT (single choice), EXTRA_CHECKBOX (multiple choice)
    "name": "size",                  // Can be: size, sauce, contain, extra, potato, panzo, type, stbpanzo, cheese,
    "values": ["value1", "value2"],  // Value names
    "prices": [0, 200],              // Additional prices for each value
    "aliases": ["v1", "v2"],         // Value name aliases for pdf export (only internal use)
    "_display": "Display text",      // Display text in search menu (optional, default: values->join) {pieces} => db
    "_hide": true,                   // Hide from search menu (optional, default: false)
    "_comment" : "Lorem ipsum",      // Text below the input field (optional, default: empty string)
    "_extra" : false                 // If true the item will be notified as 'extra'. (optional, default: false)
}
```

#### 5.2.1 Megkötések:

- Ugyan annyi `values` értéknek kell lennie mint a `prices`-nak és az `aliases`-nak.
- Ha hibás a json, akkor a termékek listában "Invalid descriptor" szöveg jelenik meg a termék leírásában.
- Minifyolva kell beilleszteni a jsont. (egy sor legyen)
- A name értéknek egyedinek kell legyen. Ha nem találod itt a megfelelőt, akkor kérj újat.

#### 5.2.2 Példák

##### 5.2.2.1 Americano példa:
```json
[
    {
        "type":"EXTRA_SELECT",
        "name":"sauce",
        "_hide":false,
        "values":[
            "Ketchup",
            "Mustár",
            "Majonéz",
            "Speckó szósz",
            "Sajt szósz",
            "BBQ szósz",
            "Csípős szósz"
        ],
        "prices":[
            0,
            0,
            0,
            0,
            50,
            50,
            50
        ],
        "aliases":[
            "KUPC",
            "MUST",
            "MAJO",
            "SPEC",
            "SAJT",
            "BBQ",
            "CSIP"
        ]
    },
    {
        "type":"EXTRA_SELECT",
        "name":"extra",
        "_hide":true,
        "values":[
            "Pirított hagyma",
            "Csemege uborka",
            "Cheddar sajt",
            "Tükörtojás",
            "Jalapeño paprika",
            "Bacon"
        ],
        "prices":[
            0,
            0,
            50,
            50,
            50,
            100
        ],
        "aliases":[
            "PH",
            "CSUBI",
            "CHED",
            "TOJ",
            "JAL",
            "BAC"
        ]
    }
]
```

##### 5.2.2.2 Dzsájrosz példa:
```json
[
    {
        "type":"EXTRA_CHECKBOX",
        "name":"onion",
        "_hide":true,
        "values":[
            "Lilahagyma",
            "Pirított hagyma"
        ],
        "prices":[
            0,
            0
        ],
        "aliases":[
            "LH",
            "PH"
        ]
    },
    {
        "type":"EXTRA_CHECKBOX",
        "name":"sauce",
        "_hide":true,
        "values":[
            "Sima",
            "Foghagymás",
            "Csípős"
        ],
        "prices":[
            0,
            0,
            0
        ],
        "aliases":[
            "SIMA",
            "FH",
            "CSÍP"
        ]
    },
    {
        "type":"EXTRA_SELECT",
        "name":"cheese",
        "_hide":true,
        "values":[
            "Nem kérek",
            "Extra sajt",
            "Dupla sajt"
        ],
        "prices":[
            0,
            100,
            200
        ],
        "aliases":[
            "NE",
            "EXTRA x1",
            "Extra x2"
        ]
    },
    {
        "type":"EXTRA_CHECKBOX",
        "name":"extra",
        "_hide":true,
        "values":[
            "Extra hús",
            "Extra csípős"
        ],
        "prices":[
            200,
            50
        ],
        "aliases":[
            "HÚS",
            "CSÍP"
        ],
        "_comment":"Extra csípős: mindenféle különleges esszenciát bevetve egyenesen a pokolból"
    }
]
```
