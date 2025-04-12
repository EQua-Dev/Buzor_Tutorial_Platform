package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.wallet.presentation

data class FundingState(
    val amount: String = "",
    val question1: String = "",
    val answer1: String = "",
    val question2: String = "",
    val answer2: String = "",
    val isVerifying: Boolean = false,
    val error: String? = null
)
