package hu.kirdev.schpincer.web.component

/**
 * Variables starting with _ (underscore) are meant to be optional.
 * I know this is a bad practice, but it is part of the API, so changing it is not recommended.
 */
data class CustomComponentModel (
    val type: String = "",
    val name: String = "",
    val values: List<String> = listOf(),
    val prices: List<Int> = listOf(),
    val aliases: List<String> = listOf(),
    val _display: String? = null,
    val _hide: Boolean? = null,
    val _comment: String? = null,
    val min: Int = 0,
    val max: Int = 0
)

data class CustomComponentModelList(var models: List<CustomComponentModel> = listOf())
