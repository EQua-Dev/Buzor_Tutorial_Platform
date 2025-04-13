package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.payments.presentation

import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.WalletHistory

sealed class TeacherPaymentsEvent {
    data class OnFilterChange(val filter: String) : TeacherPaymentsEvent()
    object ToggleBalanceVisibility : TeacherPaymentsEvent()
    data class OnTransactionClick(val transaction: WalletHistory) : TeacherPaymentsEvent()
    object OnDismissDialog : TeacherPaymentsEvent()
}