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
    DEFAULT(false, "Default",
            Arrays.asList("ID", "NÉV", "IDÖSÁV", "TERMÉK", "EXTRA", "MEGJEGYZÉS", "ÁR"),
            new int[] {   3,    8,     5,        10,       5,       10,           4   },
            Arrays.asList(
                    (order) -> "" + order.getArtificialId(),
                    OrderEntity::getUserName,
                    OrderEntity::getIntervalMessage,
                    OrderEntity::getName,
                    OrderEntity::getExtra,
                    OrderEntity::getComment,
                    (order) -> "" + order.getPrice()
            )),

    DZSAJROSZ(false, "Dzsájrosz",
            Arrays.asList("ID", "NÉV", "SZOBA", "TERMÉK", "HAGYMA", "ÖNTET", "SAJT", "EXTRA", "MEGJEGYZÉS", "ÁR"), 
            new int[] {   3,    8,     5,       10,       8,        10,      8,      10,      20,           4   },
            Arrays.asList(
                    (order) -> "" + order.getArtificialId(),
                    OrderEntity::getUserName,
                    (order) -> order.getRoom().replace("SCH ", ""),
                    OrderEntity::getName,
                    (order) -> order.getExtra().split("; ", 2)[0],
                    (order) -> order.getExtra().indexOf(';') != -1 ? order.getExtra().split(";", 3)[1] : "-",
                    (order) -> order.getExtra().indexOf(';') != order.getExtra().lastIndexOf(';') ? order.getExtra().split("; ", 4)[2] : "-",
                    (order) -> order.getExtra().indexOf(';') != order.getExtra().lastIndexOf(';') ? order.getExtra().split("; ", 5)[3] : "-",
                    OrderEntity::getComment,
                    (order) -> "" + order.getPrice()
            )),
    
    AMERICANO(false, "Americano", 
            Arrays.asList("ID", "NÉV", "IDÖSÁV", "TERMÉK", "!SZÓSZOK", "MEGJEGYZÉS", "ÁR"),
            new int[] {   3,    8,     5,        10,       5,          10,           4   },
            Arrays.asList(
                    (order) -> "" + order.getArtificialId(),
                    OrderEntity::getUserName,
                    OrderEntity::getIntervalMessage,
                    OrderEntity::getName,
                    OrderEntity::getExtra,
                    OrderEntity::getComment,
                    (order) -> "" + order.getPrice()
            )),
    PIZZASCH(false, "Pizzasch",
            Arrays.asList("ID", "NÉV", "IDÖSÁV", "TERMÉK", "MEGJEGYZÉS", "ÁR"),
            new int[] {   3,    8,     5,        10,       10,           4   },
            Arrays.asList(
                    (order) -> "" + order.getArtificialId(),
                    OrderEntity::getUserName,
                    OrderEntity::getIntervalMessage,
                    (order) -> order.getName() + " " + order.getExtra().toUpperCase(),
                    OrderEntity::getComment,
                    (order) -> "" + order.getPrice()
            )),

    FOODEX(true, "Foodex", 
            Arrays.asList("ID", "NÉV", "TERMÉK", "MEGJEGYZÉS", "ÁR"), 
            new int[] {   3,    8,     5,        10,           4   },
            Arrays.asList(
                    (order) -> "" + order.getArtificialId(),
                    OrderEntity::getUserName,
                    OrderEntity::getName,
                    OrderEntity::getComment,
                    (order) -> "" + order.getPrice()
            )),
    ;

    private final boolean portrait;
    private final String displayName;
    private final List<String> header;
    private final int[] widths;
    private final List<Function<OrderEntity, String>> fields;
    
}
