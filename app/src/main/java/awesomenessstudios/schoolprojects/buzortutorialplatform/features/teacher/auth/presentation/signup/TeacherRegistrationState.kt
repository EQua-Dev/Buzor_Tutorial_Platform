package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.auth.presentation.signup

data class TeacherRegistrationState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val phoneNumber: String = "",
    val subjects: List<String> = emptyList(),
    val otp: String = "",
    val isLoading: Boolean = false,
    val isRegistrationSuccessful: Boolean = false,
    val isOtpSent: Boolean = false,
    val isVerificationSuccessful: Boolean = false,
    val errorMessage: String? = null
)