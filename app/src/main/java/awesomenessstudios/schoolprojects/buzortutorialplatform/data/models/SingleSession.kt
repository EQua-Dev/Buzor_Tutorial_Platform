package awesomenessstudios.schoolprojects.buzortutorialplatform.data.models

import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Constants

data class SingleSession(
    override val id: String = "",
    override val courseId: String = "",
    override val teacherId: String = "",
    override val students: List<String> = listOf(),
    override val startTime: String = "",
    override val type: String = "",
    val studentId: String = "",
    override val sessionLink: String = Constants.ZOOM_LINK,
    override val price: String = "",
    override val dateCreated: String = "",
    override val status: String = ""
): Session
