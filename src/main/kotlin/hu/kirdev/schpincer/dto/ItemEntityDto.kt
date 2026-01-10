package hu.kirdev.schpincer.dto

import hu.kirdev.schpincer.model.*
import java.lang.Integer.max
import java.lang.Integer.min
import java.time.Instant

class ItemEntityDto(base: ItemEntity, opening: OpeningEntity?, loggedin: Boolean, explicitMapping: Boolean) {
    val id: Long = base.id
    val name: String = base.name
    val description: String = base.description
    val ingredients: String = base.ingredients
    val detailsConfigJson: String = base.detailsConfigJson
    val price: Int = if (loggedin) base.price else -1
    val orderable: Boolean = base.orderable && opening != null && opening.isInInterval(Instant.now().toEpochMilli())
    val service: Boolean = base.service
    val personallyOrderable: Boolean = base.personallyOrderable
    val imageName: String = base.imageName ?: "/cdn/image/blank-null-item.jpg"
    var circleId: Long = base.circle?.id ?: 0L
    var circleAlias: String = base.circle?.alias ?: "404"
    var circleName: String = base.circle?.displayName ?: "Not Attached"
    var circleColor: String = base.circle?.cssClassName ?: ""
    val nextOpeningDate: Long = opening?.dateStart ?: 0
    val timeWindows: List<TimeWindowEntity> = if (loggedin) {
        (opening?.timeWindows ?: listOf()).filter { explicitMapping || it.extraItemCount >= 0 }
    } else {
        listOf()
    }
    val orderStatus: ItemOrderableStatus = ItemOrderableStatus.OK
    val flag: Int = base.flag
    val circleIcon: String = base.circle?.backgroundUrl ?: ""
    val categoryMax: Int = if (opening != null) {
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
    val discountPrice: Int = base.discountPrice
    val keywords: String = base.keywords?.replace(",", "") ?: ""
    val outOfStock: Boolean = orderable && (((opening?.timeWindows ?: listOf()).all { it.normalItemCount <= 0 }) || (categoryMax == 0))
}
