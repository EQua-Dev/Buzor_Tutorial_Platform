package awesomenessstudios.schoolprojects.buzortutorialplatform.data.models

data class WalletHistory(
    val id: String = "",
    val walletId: String = "",
    val transactionType: String = "",
    val amount: Double = 0.0,
    val description: String = "",
    val sender: String = "",
    val receiver: String = "",
    val walletOwner: String = "",
    val transactionLocation: String = "",
    val dateCreated: String = ""
)
