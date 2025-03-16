package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.auth.presentation.signup

import android.app.Activity

sealed class StudentRegistrationEvent {
    data class FirstNameChanged(val firstName: String) : StudentRegistrationEvent()
    data class LastNameChanged(val lastName: String) : StudentRegistrationEvent()
    data class EmailChanged(val email: String) : StudentRegistrationEvent()
    data class PhoneNumberChanged(val phoneNumber: String) : StudentRegistrationEvent()
    data class PasswordChanged(val password: String) : StudentRegistrationEvent()
    data class GradeChanged(val grade: String) : StudentRegistrationEvent()
    data class OtpChanged(val otp: String) : StudentRegistrationEvent()

    data class Register(val activity: Activity) : StudentRegistrationEvent()
    object VerifyOtp : StudentRegistrationEvent()

}