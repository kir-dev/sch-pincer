package hu.kirdev.schpincer.dto

import hu.kirdev.schpincer.model.CircleEntity
import hu.kirdev.schpincer.model.OpeningEntity

data class CircleEntityInfoDto(
        val circleEntity: CircleEntity,
        var nextOpening: OpeningEntity? = null
)