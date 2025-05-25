package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher

import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Student
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Teacher

data class TeacherProfileState(
    val teacher: Teacher = Teacher(),
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val errorMessage: String? = null,
    val isUpdateSuccessful: Boolean = false
)
