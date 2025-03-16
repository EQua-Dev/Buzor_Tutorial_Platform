package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.auth.presentation.signup

import android.app.Activity
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Student
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Common.mAuth
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Common.studentsCollectionRef
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class StudentRegistrationViewModel @Inject constructor() : ViewModel() {
    private val _state = mutableStateOf(StudentRegistrationState())
    val state: State<StudentRegistrationState> = _state

    private var verificationId: String? = null // Store the verification ID from Firebase


    fun onEvent(event: StudentRegistrationEvent) {
        when (event) {
            is StudentRegistrationEvent.FirstNameChanged -> {
                _state.value = _state.value.copy(firstName = event.firstName)
            }
            is StudentRegistrationEvent.LastNameChanged -> {
                _state.value = _state.value.copy(lastName = event.lastName)
            }
            is StudentRegistrationEvent.EmailChanged -> {
                _state.value = _state.value.copy(email = event.email)
            }
            is StudentRegistrationEvent.PasswordChanged -> {
                _state.value = _state.value.copy(password = event.password)
            }
            is StudentRegistrationEvent.PhoneNumberChanged -> {
                _state.value = _state.value.copy(phoneNumber = event.phoneNumber)
            }
            is StudentRegistrationEvent.GradeChanged -> {
                _state.value = _state.value.copy(grade = event.grade)
            }
            is StudentRegistrationEvent.Register -> {
                registerStudent(event.activity)
            }
            is StudentRegistrationEvent.OtpChanged -> {
                _state.value = _state.value.copy(otp = event.otp)
            }
            StudentRegistrationEvent.VerifyOtp -> {
                verifyOtp()
            }
        }
    }

    private fun registerStudent(activity: Activity) {
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)

        val email = _state.value.email
        val password = _state.value.password

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = mAuth.currentUser?.uid ?: ""
                    saveStudentDetails(userId, activity)
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = task.exception?.message ?: "Registration failed"
                    )
                }
            }
    }

    private fun saveStudentDetails(userId: String, activity: Activity) {
        val student = Student(
            id = userId,
            firstName = _state.value.firstName,
            lastName = _state.value.lastName,
            email = _state.value.email,
            phoneNumber = _state.value.phoneNumber,
            password = _state.value.password,
            grade = _state.value.grade
        )

        studentsCollectionRef.document(userId)
            .set(student)
            .addOnSuccessListener {
                _state.value = _state.value.copy(
                    isLoading = false,
//                    isRegistrationSuccessful = true
                )
                sendOtp(student.phoneNumber, activity)
            }
            .addOnFailureListener { e ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to save students details"
                )
            }
    }


    private fun sendOtp(phoneNumber: String, activity: Activity) {
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity) // Pass the current activity
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Auto-verification (e.g., SMS retriever)
//                    signInWithPhoneAuthCredential(credential)
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
                    this@StudentRegistrationViewModel.verificationId = verificationId
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
        val otp = _state.value.otp
        if (otp.length != 6) {
            _state.value = _state.value.copy(errorMessage = "Invalid OTP")
            return
        }

        _state.value = _state.value.copy(isLoading = true, errorMessage = null)

        val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
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
        val userId = mAuth.currentUser?.uid ?: return

        studentsCollectionRef.document(userId)
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
