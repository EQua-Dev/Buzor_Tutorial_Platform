package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.profile.presentation

sealed class StudentProfileEvent {
    data class FirstNameChanged(val value: String) : StudentProfileEvent()
    data class LastNameChanged(val value: String) : StudentProfileEvent()
    data class PhoneNumberChanged(val value: String) : StudentProfileEvent()
    data class GradeChanged(val value: String) : StudentProfileEvent()
    data class PreferredSubjectsChanged(val subjects: List<String>) : StudentProfileEvent()
    data class ProfileImageChanged(val url: String) : StudentProfileEvent()

    object ToggleEditMode : StudentProfileEvent()
    object SaveProfile : StudentProfileEvent()
    object LoadProfile : StudentProfileEvent()
}
