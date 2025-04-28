package awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.sessionrepo

import awesomenessstudios.schoolprojects.buzortutorialplatform.data.enums.SessionStatus
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Escrow
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.GroupSession
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.SingleSession
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    suspend fun createSingleSession(session: SingleSession)

    fun getUpcomingGroupSessions(teacherId: String): Flow<List<GroupSession>>
    fun getUpcomingSingleSessions(teacherId: String): Flow<List<SingleSession>>
    fun getUpcomingGroupSessionsForCourse(courseId: String): Flow<List<GroupSession>>
    fun getPendingSingleSessionRequests(teacherId: String): Flow<List<SingleSession>>
    fun updateSessionStatus(sessionId: String, status: SessionStatus, onResult: (Boolean) -> Unit)
    fun getMyGroupSessions(studentId: String): Flow<List<GroupSession>>
    fun getMySingleSessions(studentId: String): Flow<List<SingleSession>>
    fun getAvailableGroupSessions(studentId: String): Flow<List<GroupSession>>
    fun getPendingRequests(studentId: String): Flow<List<SingleSession>>
    suspend fun enrollUserInGroupSession(sessionId: String, userId: String): Result<Unit>
//    fun getStudentPendingSingleSessionRequests(studentId: String): Flow<List<SingleSession>>


}
