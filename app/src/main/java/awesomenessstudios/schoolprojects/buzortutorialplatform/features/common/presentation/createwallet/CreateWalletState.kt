package awesomenessstudios.schoolprojects.buzortutorialplatform.features.common.presentation.createwallet

data class CreateWalletState(
    val loggedInUser: String = "",
    val securityQuestion1: String = "",
    val securityAnswer1: String = "",
    val securityQuestion2: String = "",
    val securityAnswer2: String = "",
    val walletAddressComplexity: String = "",
    val isLoading: Boolean = false,
    val isWalletCreated: Boolean = false,
    val errorMessage: String? = null,
    val userRole: String? = null

)
