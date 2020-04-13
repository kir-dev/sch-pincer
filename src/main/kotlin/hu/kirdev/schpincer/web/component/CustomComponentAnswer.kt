package hu.kirdev.schpincer.web.component

data class CustomComponentAnswer(
        val type: String = "",
        val name: String = "",
        val selected: List<Int> = listOf()
)

data class CustomComponentAnswerList(
        val answers: List<CustomComponentAnswer> = listOf()
)
