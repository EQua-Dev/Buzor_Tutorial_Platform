package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.auth.presentation.signup

data class StudentRegistrationState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val grade: String = "",
    val otp: String = "",
    val isLoading: Boolean = false,
    val isRegistrationSuccessful: Boolean = false,
    val isOtpSent: Boolean = false,
    val isVerificationSuccessful: Boolean = false,
    val errorMessage: String? = null
)