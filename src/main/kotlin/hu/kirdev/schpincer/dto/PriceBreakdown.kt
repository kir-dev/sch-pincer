package hu.kirdev.schpincer.dto

data class PriceBreakdown(
    var orderId: Long,
    var prices: Map<String, Int>
)
