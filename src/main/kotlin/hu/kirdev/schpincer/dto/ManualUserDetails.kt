package hu.kirdev.schpincer.dto

import hu.kirdev.schpincer.model.CardType

data class ManualUserDetails(
        var id: String,
        var name: String,
        var room: String,
        var card: CardType
)