package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.sessions.presentation

import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Escrow
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.GroupSession
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Session
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.SingleSession

data class StudentSessionState(
    val myGroupSessions: List<GroupSession> = emptyList(),
    val mySingleSessions: List<SingleSession> = emptyList(),
    val availableGroupSessions: List<GroupSession> = emptyList(),
    val requests: List<SingleSession> = emptyList(),
    val courseTitles: Map<String, String> = emptyMap(),
    val escrowSessions: Map<String, Session> = emptyMap()
)
