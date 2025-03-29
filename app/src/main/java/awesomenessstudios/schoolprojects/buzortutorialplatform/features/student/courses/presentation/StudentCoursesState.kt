package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.courses.presentation

import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Course

data class StudentCoursesState(
    val courses: List<Course> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val studentGrade: String? = null
)