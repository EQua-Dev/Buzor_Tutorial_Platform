package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.wallet.presentation

import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.WalletHistory
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.payments.presentation.TeacherPaymentsEvent

sealed class WalletEvent {
    data class OnFilterChange(val filter: String) : WalletEvent()
    object ToggleBalanceVisibility : WalletEvent()
    data class OnTransactionClick(val transaction: WalletHistory) : WalletEvent()
    object OnDismissDialog : WalletEvent()
}