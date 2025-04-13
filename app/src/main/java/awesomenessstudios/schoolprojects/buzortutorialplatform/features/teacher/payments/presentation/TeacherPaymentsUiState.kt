package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.payments.presentation

import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Wallet
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.WalletHistory

data class TeacherPaymentsUiState(
    var walletState: Wallet? = null,
    val balance: String = "0.0",
    val history: List<WalletHistory> = emptyList(),
    val filter: String = "All",
    val isBalanceVisible: Boolean = true,
    val selectedTransaction: WalletHistory? = null,
    val showFundingDialog: Boolean = false
)