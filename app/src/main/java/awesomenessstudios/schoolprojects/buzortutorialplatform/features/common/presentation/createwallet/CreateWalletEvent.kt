package awesomenessstudios.schoolprojects.buzortutorialplatform.features.common.presentation.createwallet

sealed class CreateWalletEvent {
    data class SecurityQuestion1Changed(val question: String) : CreateWalletEvent()
    data class SecurityAnswer1Changed(val answer: String) : CreateWalletEvent()
    data class SecurityQuestion2Changed(val question: String) : CreateWalletEvent()
    data class SecurityAnswer2Changed(val answer: String) : CreateWalletEvent()
    data class WalletAddressComplexityChanged(val algorithm: String) : CreateWalletEvent()
    object CreateWallet : CreateWalletEvent()
}