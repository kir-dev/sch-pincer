package hu.kirdev.schpincer.model

enum class OrderStatus {
    ACCEPTED,
    INTERPRETED,
    SHIPPED,
    CANCELLED,
    UNKNOWN;

    companion object {
        operator fun get(status: String): OrderStatus {
            for (os in values())
                if (os.name.equals(status, ignoreCase = true))
                    return os
            return UNKNOWN
        }
    }
}
