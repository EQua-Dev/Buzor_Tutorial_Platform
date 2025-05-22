package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.wallet.fundwallet.presentation

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.Result
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.enums.TransactionTypes
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Wallet
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.payments.fundwallet.presentation.WithdrawState
import awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.walletrepo.WalletRepository
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Constants.WALLETS_REF
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.HelpMe
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.LocationUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.inject.Inject

@HiltViewModel
class WithdrawViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val context: Application,
    private val walletRepository: WalletRepository,
    private val locationUtils: LocationUtils
) : ViewModel() {

    var state by mutableStateOf(WithdrawState())

    fun onAmountChange(value: String) {
        state = state.copy(amount = value)
    }

    fun onQuestion1Change(value: String) {
        state = state.copy(answer1 = value)
    }

    fun onQuestion2Change(value: String) {
        state = state.copy(answer2 = value)
    }

    private fun hash(input: String, algorithm: String): String {
        return try {
            val digest = MessageDigest.getInstance(algorithm)
            val hashBytes = digest.digest(input.toByteArray())
            BigInteger(1, hashBytes).toString(16).padStart(32, '0')
        } catch (e: NoSuchAlgorithmException) {
            ""
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun verifyAndFund(
        activity: FragmentActivity,
        wallet: Wallet,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val hash1 = hash(wallet.securityQuestion1 + state.answer1, wallet.hashType)
        val hash2 = hash(wallet.securityQuestion2 + state.answer2, wallet.hashType)
        val combinedHash = hash(hash1 + hash2, wallet.hashType)

          if (combinedHash != wallet.securityHash) {
              onFailure("Security answers don't match")
              return
          }
        HelpMe.promptBiometric(
            activity = activity,
            title = "Authorize Transaction to add ₦${state.amount}",
            onSuccess = {
                fundWallet(wallet, onSuccess, onFailure)
            },
            onNoHardware = {
                fundWallet(wallet, onSuccess, onFailure)
            }
        )
    }


    @RequiresApi(Build.VERSION_CODES.P)
    fun verifyAndWithdraw(
        activity: FragmentActivity,
        wallet: Wallet,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val hash1 = hash(wallet.securityQuestion1 + state.answer1, wallet.hashType)
        val hash2 = hash(wallet.securityQuestion2 + state.answer2, wallet.hashType)
        val combinedHash = hash(hash1 + hash2, wallet.hashType)
//
//          if (combinedHash != wallet.securityHash) {
//              onFailure("Security answers don't match")
//              return
//          }
        HelpMe.promptBiometric(
            activity = activity,
            title = "Authorize Transaction to withdraw ₦${state.amount}",
            onSuccess = {
                withdrawWallet(wallet, onSuccess, onFailure)
            },
            onNoHardware = {
                withdrawWallet(wallet, onSuccess, onFailure)
            }
        )
    }

    private fun fundWallet(wallet: Wallet, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val newBalance = wallet.balance.toDouble() - state.amount.toDouble()

        try {
            locationUtils.getCurrentLocation()
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val locationAddress = locationUtils.getLocationAddress(location)


                        viewModelScope.launch {
                            walletRepository.creditWallet(
                                userId = auth.currentUser!!.uid,
                                amount = state.amount.toDouble(),
                                description = "Wallet fund",
                                sender = auth.currentUser!!.uid,
                            ).getOrThrow()
                        }
                    } else {
                        /* _state.value = _state.value.copy(
                             isLoading = false,
                             errorMessage = "Unable to fetch location"
                         )*/
                    }
                }
                .addOnFailureListener { e ->
                    /* _state.value = _state.value.copy(
                         isLoading = false,
                         errorMessage = e.message ?: "Failed to get location"
                     )*/
                }
            onSuccess()
            Result.Success(Unit)
        } catch (e: Exception) {
            onFailure(e.localizedMessage?.toString() ?: "Error encountered")
            Result.Failure(e)
        }


    }

    private fun withdrawWallet(wallet: Wallet, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val newBalance = wallet.balance.toDouble() - state.amount.toDouble()

        try {
            locationUtils.getCurrentLocation()
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val locationAddress = locationUtils.getLocationAddress(location)


                        viewModelScope.launch {
                            walletRepository.debitWallet(
                                userId = auth.currentUser!!.uid,
                                amount = state.amount.toDouble(),
                                description = "Wallet withdrawal",
                                location = locationAddress,
                                receiver = auth.currentUser!!.uid
                            ).getOrThrow()
                        }
                    } else {
                        /* _state.value = _state.value.copy(
                             isLoading = false,
                             errorMessage = "Unable to fetch location"
                         )*/
                    }
                }
                .addOnFailureListener { e ->
                    /* _state.value = _state.value.copy(
                         isLoading = false,
                         errorMessage = e.message ?: "Failed to get location"
                     )*/
                }
            onSuccess()
            Result.Success(Unit)
        } catch (e: Exception) {
            onFailure(e.localizedMessage?.toString() ?: "Error encountered")
            Result.Failure(e)
        }


    }
    /*firestore.collection(WALLETS_REF).document(wallet.id)
        .update("balance", newBalance.toString())
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onFailure(it.message ?: "Funding failed") }*/

}

