package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.courses.createcourse

import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.CourseSection

data class CreateCourseState(
    val loggedInUser: String = "",
    val subject: String = "",
    val targetGrades: List<String> = listOf(),
    val title: String = "",
    val description: String = "",
    val price: String = "",
    val coverImage: String = "",
    val sections: List<CourseSection> = listOf(CourseSection()),
    val allowPrivateSessions: Boolean = false,
    val privateSessionPrice: String = "",
    val allowGroupSessions: Boolean = false,
    val groupSessionPrice: String = "",
    val maxSeats: Int = 0,
    val sessionDate: String = "",
    val sessionTime: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isCourseCreated: Boolean = false
)