package hu.gerviba.webschop.dto

data class OpeningEntityDto(
    var timeIntervals: Int = 4,

    val dateStart: String = "",
    val dateEnd: String = "",
    val orderStart: String = "",
    val orderEnd: String = "",
    val feeling: String = "",

    var maxOrder: Int = 120,
    var maxOrderPerInterval: Int = 15,
    var maxExtraPerInterval: Int = 0,
    var intervalLength: Int = 30,

    var maxAlpha: Int = 0,
    var maxBeta: Int = 0,
    var maxGamma: Int = 0,
    var maxDelta: Int = 0,
    var maxLambda: Int = 0
)

