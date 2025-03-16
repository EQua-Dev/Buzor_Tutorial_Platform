package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.auth.presentation.signup

import android.app.Activity
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.auth.presentation.signup.StudentRegistrationEvent

sealed class TeacherRegistrationEvent {
    data class FirstNameChanged(val firstName: String) : TeacherRegistrationEvent()
    data class LastNameChanged(val lastName: String) : TeacherRegistrationEvent()
    data class EmailChanged(val email: String) : TeacherRegistrationEvent()
    data class PasswordChanged(val password: String) : TeacherRegistrationEvent()
    data class PhoneNumberChanged(val phoneNumber: String) : TeacherRegistrationEvent()
    data class SubjectsChanged(val subject: String) : TeacherRegistrationEvent()
    data class OtpChanged(val otp: String) : TeacherRegistrationEvent()

    data class Register(val activity: Activity) : TeacherRegistrationEvent()
    object VerifyOtp : TeacherRegistrationEvent()

}