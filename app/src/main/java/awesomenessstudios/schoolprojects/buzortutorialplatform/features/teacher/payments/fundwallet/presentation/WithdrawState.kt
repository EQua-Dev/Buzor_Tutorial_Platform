package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.payments.fundwallet.presentation

data class WithdrawState(
    val amount: String = "",
    val question1: String = "",
    val answer1: String = "",
    val question2: String = "",
    val answer2: String = "",
    val isVerifying: Boolean = false,
    val error: String? = null
)
