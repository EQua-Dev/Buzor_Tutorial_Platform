package awesomenessstudios.schoolprojects.buzortutorialplatform.data.models

data class SingleSession(
    override val id: String = "",
    override val courseId: String = "",
    override val teacherId: String = "",
    override val startTime: String = "",
    override val type: String = "",
    val studentId: String = "",
    override val sessionLink: String = "",
    override val price: String = "",
    override val dateCreated: String = "",
    override val status: String = ""
): Session
