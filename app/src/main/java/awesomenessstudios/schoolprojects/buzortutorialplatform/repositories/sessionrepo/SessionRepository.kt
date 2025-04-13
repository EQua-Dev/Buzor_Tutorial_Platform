package awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.sessionrepo

import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.GroupSession
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.SingleSession
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun getUpcomingGroupSessions(teacherId: String): Flow<List<GroupSession>>
    fun getUpcomingSingleSessions(teacherId: String): Flow<List<SingleSession>>
}
