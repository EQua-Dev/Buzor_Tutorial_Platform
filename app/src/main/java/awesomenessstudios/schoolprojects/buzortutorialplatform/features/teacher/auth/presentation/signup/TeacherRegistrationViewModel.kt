package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.auth.presentation.signup

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Teacher
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Common.mAuth
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Common.teachersCollectionRef
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.UserPreferences
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltViewModel
class TeacherRegistrationViewModel @Inject constructor(private val userPreferences: UserPreferences) :
    ViewModel() {
    private val _state = mutableStateOf(TeacherRegistrationState())
    val state: State<TeacherRegistrationState> = _state

    private var verificationId: String? = null // Store the verification ID from Firebase
    private var teacherId: String? = null


    fun onEvent(event: TeacherRegistrationEvent) {
        when (event) {
            is TeacherRegistrationEvent.FirstNameChanged -> {
                _state.value = _state.value.copy(firstName = event.firstName)
            }

            is TeacherRegistrationEvent.LastNameChanged -> {
                _state.value = _state.value.copy(lastName = event.lastName)
            }

            is TeacherRegistrationEvent.EmailChanged -> {
                _state.value = _state.value.copy(email = event.email)
            }

            is TeacherRegistrationEvent.PasswordChanged -> {
                _state.value = _state.value.copy(password = event.password)
            }

            is TeacherRegistrationEvent.PhoneNumberChanged -> {
                _state.value = _state.value.copy(phoneNumber = event.phoneNumber)
            }

            is TeacherRegistrationEvent.SubjectsChanged -> {
                val updatedSubjects = _state.value.subjects.toMutableList()
                if (event.subject in updatedSubjects) {
                    updatedSubjects.remove(event.subject)
                } else {
                    updatedSubjects.add(event.subject)
                }
                _state.value = _state.value.copy(subjects = updatedSubjects)
            }

            is TeacherRegistrationEvent.Register -> {
                registerTeacher(event.activity)
            }

            is TeacherRegistrationEvent.OtpChanged -> {
                _state.value = _state.value.copy(otp = event.otp)
            }

            TeacherRegistrationEvent.VerifyOtp -> {
                verifyOtp()
            }

            TeacherRegistrationEvent.DismissError -> {
                _state.value = _state.value.copy(errorMessage = null)
            }
        }
    }

    private fun registerTeacher(activity: Activity) {
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)

        val email = _state.value.email
        val password = _state.value.password

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = mAuth.currentUser?.uid ?: ""
                    teacherId = userId
                    _state.value = _state.value.copy(newUserId = userId)
                    sendOtp(_state.value.phoneNumber, activity, userId) // Send OTP immediately after account creation
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = task.exception?.message ?: "Registration failed"
                    )
                }
            }
    }


    private fun sendOtp(phoneNumber: String, activity: Activity, userId: String) {
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity) // Pass the current activity
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Auto-verification (e.g., SMS retriever)
                    val otp = credential.smsCode // Get the OTP from the credential
                    Log.d("TAG", "onVerificationCompleted: ${credential.smsCode}")
                    if (otp != null) {
                        _state.value = _state.value.copy(otp = otp) // Directly set OTP for auto-verification
                        verifyOtpWithCredential(credential, userId)
                    }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to send OTP"
                    )
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    this@TeacherRegistrationViewModel.verificationId = verificationId
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isOtpSent = true
                    )
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyOtp() {
        val enteredOtp = _state.value.otp
        if (verificationId.isNullOrEmpty() || enteredOtp.length != 6) {
            _state.value = _state.value.copy(errorMessage = "Invalid OTP")
            return
        }

        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
        val credential = PhoneAuthProvider.getCredential(verificationId!!, enteredOtp)
        signInWithPhoneAuthCredential(credential, _state.value.newUserId)
    }

    private fun verifyOtpWithCredential(credential: PhoneAuthCredential, userId: String) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveTeacherDetails(userId) // Save details after successful phone auth
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = task.exception?.message ?: "OTP verification failed"
                    )
                }
            }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, userId: String) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Phone number verification successful, now save teacher details
                    saveTeacherDetails(userId)
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = task.exception?.message ?: "OTP verification failed"
                    )
                }
            }
    }

    private fun saveTeacherDetails(userId: String) {
        val teacher = Teacher(
            id = userId,
            firstName = _state.value.firstName,
            lastName = _state.value.lastName,
            subjects = _state.value.subjects,
            email = _state.value.email,
            phoneNumber = _state.value.phoneNumber,
            isVerified = true // Set to true after successful OTP verification
        )

        teachersCollectionRef.document(userId)
            .set(teacher)
            .addOnSuccessListener {
                _state.value = _state.value.copy(
                    isLoading = false,
                    isRegistrationSuccessful = true
                )
                viewModelScope.launch {
                    userPreferences.saveUserId(userId)
                }
            }
            .addOnFailureListener { e ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to save teacher details"
                )
            }
    }
}