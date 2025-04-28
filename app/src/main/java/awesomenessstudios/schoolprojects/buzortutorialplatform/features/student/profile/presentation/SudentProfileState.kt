package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.profile.presentation

import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Student

data class StudentProfileState(
    val student: Student = Student(),
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val errorMessage: String? = null,
    val isUpdateSuccessful: Boolean = false
)
