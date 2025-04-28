package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.sessions.presentation

import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.GroupSession
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.SingleSession
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Student

data class TeacherSessionState(
    val groupSessions: List<GroupSession> = emptyList(),
    val singleSessions: List<SingleSession> = emptyList(),
    val courseTitles: Map<String, String> = emptyMap(), // courseId -> courseTitle
    val sessionRequests: List<SingleSession> = emptyList(),
    val student: Student? = null,


)
