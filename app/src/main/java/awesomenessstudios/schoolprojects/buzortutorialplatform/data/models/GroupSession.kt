package awesomenessstudios.schoolprojects.buzortutorialplatform.data.models

data class GroupSession(
    val id: String = "",
    val courseId: String = "",
    val teacherId: String = "",
    val students: List<String> = listOf(),
    val startTime: String = "",
    val price: String = "",
    val type: String = "",
    val sessionLink: String = "",
    val maxAttendance: Int = 0,
    val dateCreated: String = ""
)
