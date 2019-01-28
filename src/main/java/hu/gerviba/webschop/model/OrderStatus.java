package hu.gerviba.webschop.model;

public enum OrderStatus {
    ACCEPTED,
    INTERPRETED,
    SHIPPED,
    CANCELLED;

    public static OrderStatus get(String status) {
        for (OrderStatus os : values())
            if (os.name().equalsIgnoreCase(status))
                return os;
        return null;
    }
}
