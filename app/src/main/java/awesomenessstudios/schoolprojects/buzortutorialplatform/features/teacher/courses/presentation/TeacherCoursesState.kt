package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.courses.presentation

import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Course


data class TeacherCoursesState(
    val courses: List<Course> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)