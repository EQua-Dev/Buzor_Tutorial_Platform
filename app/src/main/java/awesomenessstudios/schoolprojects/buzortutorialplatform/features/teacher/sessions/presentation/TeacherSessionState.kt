package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.sessions.presentation

import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.GroupSession
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.SingleSession

data class TeacherSessionState(
    val groupSessions: List<GroupSession> = emptyList(),
    val singleSessions: List<SingleSession> = emptyList()
)
