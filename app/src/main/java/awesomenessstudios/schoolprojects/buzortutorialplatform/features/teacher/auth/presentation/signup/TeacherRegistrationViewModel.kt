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

            is TeacherRegistrationEvent.OtpSent -> {
                _state.value = _state.value.copy(sentOtp = event.otp)
            }

            TeacherRegistrationEvent.VerifyOtp -> {
                verifyOtp()
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
                    saveTeacherDetails(userId, activity)
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = task.exception?.message ?: "Registration failed"
                    )
                }
            }
    }

    private fun saveTeacherDetails(userId: String, activity: Activity) {
        val teacher = Teacher(
            id = userId,
            firstName = _state.value.firstName,
            lastName = _state.value.lastName,
            subjects = _state.value.subjects,
            email = _state.value.email,
            phoneNumber = _state.value.phoneNumber,
            isVerified = false // Initially set to false
        )

        teachersCollectionRef.document(userId)
            .set(teacher)
            .addOnSuccessListener {
                _state.value = _state.value.copy(
                    isLoading = false,
//                    isRegistrationSuccessful = true
                )
                sendOtp(teacher.phoneNumber, activity, userId)
            }
            .addOnFailureListener { e ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to save teacher details"
                )
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
//                    signInWithPhoneAuthCredential(credential)
                    val otp = credential.smsCode // Get the OTP from the credential
                    Log.d("TAG", "onVerificationCompleted: ${credential.smsCode}")
                    if (otp != null) {
                        onEvent(TeacherRegistrationEvent.OtpSent(otp)) // Store the sent OTP
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
        val sentOtp = _state.value.sentOtp
        if (enteredOtp.length != 6) {
            _state.value = _state.value.copy(errorMessage = "Invalid OTP")
            return
        }

        _state.value = _state.value.copy(isLoading = true, errorMessage = null)

        /*  if (enteredOtp == sentOtp) {
              // OTP verification successful
              updateVerificationStatus()
          } else {
              // OTP verification failed
              _state.value = _state.value.copy(
                  isLoading = false,
                  errorMessage = "Incorrect OTP"
              )
          }*/
        val credential = PhoneAuthProvider.getCredential(verificationId!!, enteredOtp)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    updateVerificationStatus()
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = task.exception?.message ?: "OTP verification failed"
                    )
                }
            }
    }

    private fun updateVerificationStatus() {
        val userId = _state.value.newUserId
        viewModelScope.launch {
            userPreferences.saveUserId(userId)
        }

        teachersCollectionRef.document(userId)
            .update("isVerified", true)
            .addOnSuccessListener {
                _state.value = _state.value.copy(
                    isLoading = false,
                    isVerificationSuccessful = true,
                    isRegistrationSuccessful = true
                )
            }
            .addOnFailureListener { e ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to update verification status"
                )
            }
    }
}
