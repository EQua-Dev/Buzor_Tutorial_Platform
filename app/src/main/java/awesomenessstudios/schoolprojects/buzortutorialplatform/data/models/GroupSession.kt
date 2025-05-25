package awesomenessstudios.schoolprojects.buzortutorialplatform.data.models

data class GroupSession(
    override val id: String = "",
    override val courseId: String = "",
    override val teacherId: String = "",
    override val students: List<String> = listOf(),
    override val startTime: String = "",
    override val price: String = "",
    override val type: String = "",
    override val sessionLink: String = "",
    val maxAttendance: Int = 0,
    override val dateCreated: String = "", override val status: String? = null
) : Session


sealed interface Session {
    val id: String
    val courseId: String
    val teacherId: String
    val students: List<String>
    val startTime: String
    val price: String
    val status: String?
    val type: String
    val sessionLink: String
    val dateCreated: String
}
