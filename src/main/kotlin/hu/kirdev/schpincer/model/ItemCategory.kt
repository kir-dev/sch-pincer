package hu.kirdev.schpincer.model

enum class ItemCategory(val id: Int) {
    DEFAULT(0),
    ALPHA(1),
    BETA(2),
    GAMMA(3),
    DELTA(4),
    LAMBDA(5);

    companion object {
        fun of(category: Int): ItemCategory {
            for (value in values()) {
                if (value.id == category)
                    return value
            }
            return DEFAULT
        }
    }

}