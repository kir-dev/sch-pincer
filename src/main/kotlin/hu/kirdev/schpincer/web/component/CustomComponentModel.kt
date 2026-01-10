package hu.kirdev.schpincer.web.component

/**
 * Variables starting with _ (underscore) are meant to be optional.
 * I know this is a bad practice, but it is part of the API, so changing it is not recommended.
 */
data class CustomComponentModel(
    val type: String?,
    val name: String?,
    val values: List<String>?,
    val prices: List<Int>?,
    val aliases: List<String>?,
    val _display: String?,
    val _hide: Boolean?,
    val _comment: String?,
    val min: Int?,
    val max: Int?,
)

data class CustomComponentModelList(var models: List<CustomComponentModel> = listOf())
