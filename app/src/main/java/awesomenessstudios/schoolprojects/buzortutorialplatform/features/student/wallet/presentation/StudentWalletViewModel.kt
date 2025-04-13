package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.wallet.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.walletrepo.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentWalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository
) : ViewModel() {

    var state by mutableStateOf(WalletUiState())
        private set

    fun loadWallet(userId: String) {
        viewModelScope.launch {
            val wallet = walletRepository.getWalletByUserId(userId)
            state = state.copy(balance = wallet?.balance ?: "0.0", walletState = wallet)
            loadWalletHistory(wallet?.id ?: "")
        }
    }

    private fun loadWalletHistory(walletId: String) {
        walletRepository.observeWalletHistory(walletId) { history ->
            state = state.copy(history = history)
        }
    }

    fun onEvent(event: WalletEvent) {
        when (event) {
            is WalletEvent.OnFilterChange -> state = state.copy(filter = event.filter)
            is WalletEvent.ToggleBalanceVisibility -> state =
                state.copy(isBalanceVisible = !state.isBalanceVisible)

            is WalletEvent.OnTransactionClick -> state =
                state.copy(selectedTransaction = event.transaction)

            is WalletEvent.OnDismissDialog -> state = state.copy(selectedTransaction = null)
        }
    }

    fun dismissFundingDialog() {
        state = state.copy(showFundingDialog = false)
    }
    fun showFundingDialog() {
        state = state.copy(showFundingDialog = true)
    }
}