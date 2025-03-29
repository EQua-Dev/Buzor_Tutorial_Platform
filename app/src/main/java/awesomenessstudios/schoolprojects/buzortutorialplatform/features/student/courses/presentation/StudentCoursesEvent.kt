package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.courses.presentation

import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Course
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Student

sealed class StudentCoursesEvent {
    object LoadStudentData : StudentCoursesEvent()
    data class CoursesLoaded(val courses: List<Course>) : StudentCoursesEvent()
    data class ErrorOccurred(val message: String) : StudentCoursesEvent()
    data class StudentDataLoaded(val student: Student) : StudentCoursesEvent()
}