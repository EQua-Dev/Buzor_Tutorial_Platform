package awesomenessstudios.schoolprojects.buzortutorialplatform.data.models

data class Escrow(
    val id: String = "",
    val amount: String = "",
    val studentWallet: String = "",
    val teacherWallet: String = "",
    val sessionType: String = "",
    val sessionId: String = "",
    val courseId: String = "",
    val dateCreated: String = ""
)
