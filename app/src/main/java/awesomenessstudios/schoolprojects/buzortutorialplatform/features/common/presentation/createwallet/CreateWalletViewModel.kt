package awesomenessstudios.schoolprojects.buzortutorialplatform.features.common.presentation.createwallet

import android.location.Geocoder
import android.location.Location
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Wallet
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Common.mAuth
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Common.walletsCollectionRef
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.LocationUtils
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class CreateWalletViewModel @Inject constructor(
    private val geocoder: Geocoder,
    private val locationUtils: LocationUtils,
    private val userPreferences: UserPreferences
) : ViewModel() {


    private val _state = mutableStateOf(CreateWalletState())
    val state: State<CreateWalletState> = _state

    init {
        viewModelScope.launch {
            userPreferences.loggedInUserId.collect { userId ->
                _state.value = _state.value.copy(
                    loggedInUser = userId
                )
            }
            userPreferences.role.collect { role ->
                if (role != null) {
                    _state.value = _state.value.copy(
                        userRole = role.name
                    )
                }
            }
        }
    }

    fun onEvent(event: CreateWalletEvent) {
        when (event) {
            is CreateWalletEvent.SecurityQuestion1Changed -> {
                _state.value = _state.value.copy(securityQuestion1 = event.question)
            }

            is CreateWalletEvent.SecurityAnswer1Changed -> {
                _state.value = _state.value.copy(securityAnswer1 = event.answer)
            }

            is CreateWalletEvent.SecurityQuestion2Changed -> {
                _state.value = _state.value.copy(securityQuestion2 = event.question)
            }

            is CreateWalletEvent.SecurityAnswer2Changed -> {
                _state.value = _state.value.copy(securityAnswer2 = event.answer)
            }

            is CreateWalletEvent.WalletAddressComplexityChanged -> {
                _state.value = _state.value.copy(walletAddressComplexity = event.algorithm)
            }

            CreateWalletEvent.CreateWallet -> {
                createWallet()
            }
        }
    }

    private fun createWallet() {
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)

        // Get the current location
        locationUtils.getCurrentLocation()
            .addOnSuccessListener { location ->
                if (location != null) {
                    val locationAddress = getLocationAddress(location)
                    // Proceed with wallet creation
                    saveWallet(locationAddress)
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Unable to fetch location"
                    )
                }
            }
            .addOnFailureListener { e ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to get location"
                )
            }
    }

    private fun saveWallet(locationAddress: String) {
        // Hash the security questions and answers
        val hash1 = hash(
            state.value.securityQuestion1 + state.value.securityAnswer1,
            state.value.walletAddressComplexity
        )
        val hash2 = hash(
            state.value.securityQuestion2 + state.value.securityAnswer2,
            state.value.walletAddressComplexity
        )
        val securityHash = hash(hash1 + hash2, state.value.walletAddressComplexity)


        // Save the wallet to Firestore
        val wallet = Wallet(
            id = UUID.randomUUID().toString(),
            ownerId = _state.value.loggedInUser,
            balance = "0.0",
            dateCreated = SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
            ).format(Date()),
            creationLocation = locationAddress,
            securityHash = securityHash
        )

        walletsCollectionRef.document(wallet.id)
            .set(wallet)
            .addOnSuccessListener {
                _state.value = _state.value.copy(
                    isLoading = false,
                    isWalletCreated = true
                )
            }
            .addOnFailureListener { e ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to create wallet"
                )
            }
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

    private fun getLocationAddress(location: Location): String {
        return try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            addresses?.firstOrNull()?.getAddressLine(0) ?: "Unknown Location"
        } catch (e: Exception) {
            "Unknown Location"
        }
    }
}