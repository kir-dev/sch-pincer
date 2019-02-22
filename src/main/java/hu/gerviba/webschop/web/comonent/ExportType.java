package hu.gerviba.webschop.web.comonent;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import hu.gerviba.webschop.model.OrderEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExportType {
    // Szobaszám: -SCH
    // PH, LH abbr for extra (n)
    // Fokhagymás, sima, csípős (n)
    // extra3 sajt vagy nem
    // extra hús
    DZSAJROSZ(false, "Dzsájrosz", 
            Arrays.asList("ID", "NÉV", "SZOBA", "TERMÉK", "HAGYMA", "ÖNTET", "SAJT", "EXTRA", "MEGJEGYZÉS", "ÁR"), 
            new int[] {   3,    8,     5,       10,       8,        10,      8,      10,      20,           4   },
            Arrays.asList(
                    (order) -> "" + order.getArtificialId(),
                    (order) -> order.getUserName(),
                    (order) -> order.getRoom().replace("SCH ", ""),
                    (order) -> order.getName(),
                    (order) -> order.getExtra().split("; ", 2)[0],
                    (order) -> order.getExtra().indexOf(';') != -1 ? order.getExtra().split(";", 3)[1] : "-",
                    (order) -> order.getExtra().indexOf(';') != order.getExtra().lastIndexOf(';') ? order.getExtra().split("; ", 4)[2] : "-",
                    (order) -> order.getExtra().indexOf(';') != order.getExtra().lastIndexOf(';') ? order.getExtra().split("; ", 5)[3] : "-",
                    (order) -> order.getComment(),
                    (order) -> "" + order.getPrice()
            )),
    
    AMERICANO(false, "Americano", 
            Arrays.asList("ID", "NÉV", "IDŐSÁV", "TERMÉK", "!SZÓSZOK", "MEGJEGYZÉS", "ÁR"), 
            new int[] {   3,    8,     5,        10,       5,          10,           4   },
            Arrays.asList(
                    (order) -> "" + order.getArtificialId(),
                    (order) -> order.getUserName(),
                    (order) -> order.getIntervalMessage(),
                    (order) -> order.getName(),
                    (order) -> order.getExtra(),
                    (order) -> order.getComment(),
                    (order) -> "" + order.getPrice()
            )),
//    PIZZASCH1,
//    PIZZASCH2,
    
    FOODEX(true, "Foodex", 
            Arrays.asList("ID", "NÉV", "TERMÉK", "MEGJEGYZÉS", "ÁR"), 
            new int[] {   3,    8,     5,        10,           4   },
            Arrays.asList(
                    (order) -> "" + order.getArtificialId(),
                    (order) -> order.getUserName(),
                    (order) -> order.getName(),
                    (order) -> order.getComment(),
                    (order) -> "" + order.getPrice()
            )),
    ;

    private final boolean portrait;
    private final String displayName;
    private final List<String> header;
    private final int[] widths;
    private final List<Function<OrderEntity, String>> fields;
    
}
