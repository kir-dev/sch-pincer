package hu.kirdev.schpincer.dto

import hu.kirdev.schpincer.model.*
import java.lang.Integer.max
import java.lang.Integer.min

class ItemEntityDto(base: ItemEntity, opening: OpeningEntity?, loggedin: Boolean, explicitMapping: Boolean) {
    val id: Long
    val name: String
    val description: String
    val ingredients: String
    val detailsConfigJson: String
    val price: Int
    val orderable: Boolean
    val service: Boolean
    val personallyOrderable: Boolean
    val imageName: String
    var circleId: Long
    var circleAlias: String
    var circleName: String
    var circleColor: String
    val nextOpeningDate: Long
    val timeWindows: List<TimeWindowEntity>
    val orderStatus: ItemOrderableStatus
    val flag: Int
    val circleIcon: String
    val categoryMax: Int
    val discountPrice: Int
    val keywords: String
    val outOfStock: Boolean

    init {
        id = base.id
        name = base.name
        description = base.description
        ingredients = base.ingredients
        detailsConfigJson = base.detailsConfigJson
        price = if (loggedin) base.price else -1
        orderable = base.orderable && opening != null && opening.isInInterval(System.currentTimeMillis())
        service = base.service
        personallyOrderable = base.personallyOrderable
        imageName = base.imageName ?: "/cdn/image/blank-null-item.jpg"
        nextOpeningDate = opening?.dateStart ?: 0

        timeWindows = if (loggedin) {
            (opening?.timeWindows ?: listOf()).filter { explicitMapping || it.extraItemCount >= 0 }
        } else {
            listOf()
        }

        orderStatus = ItemOrderableStatus.OK
        flag = base.flag
        discountPrice = base.discountPrice
        keywords = base.keywords?.replace(",", "") ?: ""

        circleId = base.circle?.id ?: 0L
        circleAlias = base.circle?.alias ?: "404"
        circleIcon = base.circle?.backgroundUrl ?: ""
        circleName = base.circle?.displayName ?: "Not Attached"
        circleColor = base.circle?.cssClassName ?: ""

        categoryMax = if (opening != null) {
            max(0, min(opening.maxOrder - opening.orderCount, when (ItemCategory.of(base.category)) {
                ItemCategory.ALPHA -> opening.maxAlpha - opening.usedAlpha
                ItemCategory.BETA -> opening.maxBeta - opening.usedBeta
                ItemCategory.GAMMA -> opening.maxGamma - opening.usedGamma
                ItemCategory.DELTA -> opening.maxDelta - opening.usedDelta
                ItemCategory.LAMBDA -> opening.maxLambda - opening.usedLambda
                else -> opening.maxOrderPerInterval
            }))
        } else {
            0
        }

        outOfStock = orderable && (((opening?.timeWindows ?: listOf()).all { it.normalItemCount == 0 }) || (categoryMax == 0))
    }
}
