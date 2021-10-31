package hu.kirdev.schpincer.model

enum class OrderStatus {
    ACCEPTED,    // 1. Order completed
    INTERPRETED, // 2. They started to work on the item
    SHIPPED,     // 5. The item is delivered
    CANCELLED,   // +  Rejected by a user or the system
    UNKNOWN,
    COMPLETED,   // 3. The item is ready to ship
    HANDED_OVER, // 4. The item left the kitchen
    ;

    companion object {
        operator fun get(status: String): OrderStatus {
            for (os in values())
                if (os.name.equals(status, ignoreCase = true))
                    return os
            return UNKNOWN
        }
    }
}
