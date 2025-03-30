package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.courses.presentation

sealed interface TeacherCoursesEvent {
    data object CreateNewCourse : TeacherCoursesEvent
    data class NavigateToCourse(val courseId: String) : TeacherCoursesEvent
}
