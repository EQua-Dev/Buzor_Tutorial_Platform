package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.wallet.fundwallet.presentation

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Wallet
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.payments.fundwallet.presentation.WithdrawState
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Constants.WALLETS_REF
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.HelpMe
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.inject.Inject

@HiltViewModel
class FundingViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val context: Application
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
    fun verifyAndFund(activity: FragmentActivity, wallet: Wallet, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val hash1 = hash(wallet.securityQuestion1 + state.answer1, wallet.hashType)
        val hash2 = hash(wallet.securityQuestion2 + state.answer2, wallet.hashType)
        val combinedHash = hash(hash1 + hash2, wallet.hashType)

        if (combinedHash != wallet.securityHash) {
            onFailure("Security answers don't match")
            return
        }

        HelpMe.promptBiometric(
            activity = activity,
            title = "Authorize Transaction",
            onSuccess = {
                fundWallet(wallet, onSuccess, onFailure)
            },
            onNoHardware = {
                fundWallet(wallet, onSuccess, onFailure)
            }
        )
    }

    private fun fundWallet(wallet: Wallet, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val newBalance = wallet.balance.toDouble() + state.amount.toDouble()
        firestore.collection(WALLETS_REF).document(wallet.id)
            .update("balance", newBalance.toString())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.message ?: "Funding failed") }
    }
}

