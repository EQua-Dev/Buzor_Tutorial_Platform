package awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.sessionrepo

import awesomenessstudios.schoolprojects.buzortutorialplatform.data.enums.SessionStatus
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Escrow
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.GroupSession
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.SingleSession
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Constants.COURSES_REF
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Constants.ESCROW_REF
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Constants.GROUP_SESSIONS_REF
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Constants.SINGLE_SESSIONS_REF
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : SessionRepository {

    override fun getUpcomingGroupSessions(teacherId: String): Flow<List<GroupSession>> =
        callbackFlow {
            val now = System.currentTimeMillis()
            val listener = firestore.collection(GROUP_SESSIONS_REF)
                .whereEqualTo("teacherId", teacherId)
                .whereGreaterThan("startTime", now.toString())
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val sessions = snapshot?.toObjects(GroupSession::class.java) ?: listOf()
                    trySend(sessions)
                }

            awaitClose { listener.remove() }
        }

    override fun getUpcomingSingleSessions(teacherId: String): Flow<List<SingleSession>> =
        callbackFlow {
            val now = System.currentTimeMillis()
            val listener = firestore.collection(SINGLE_SESSIONS_REF)
                .whereEqualTo("teacherId", teacherId)
                .whereEqualTo("status", SessionStatus.ACCEPTED.name)
                .whereGreaterThan("startTime", now.toString())
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val sessions = snapshot?.toObjects(SingleSession::class.java) ?: listOf()
                    trySend(sessions)
                }

            awaitClose { listener.remove() }
        }

    override fun getUpcomingGroupSessionsForCourse(courseId: String): Flow<List<GroupSession>> =
        callbackFlow {
            val now = System.currentTimeMillis()
            val listener = firestore.collection(GROUP_SESSIONS_REF)
                .whereEqualTo("courseId", courseId)
                .whereGreaterThan("startTime", now.toString())
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val sessions = snapshot?.toObjects(GroupSession::class.java) ?: listOf()
                    trySend(sessions)
                }

            awaitClose { listener.remove() }
        }

    override suspend fun createSingleSession(session: SingleSession) {
        firestore.collection(SINGLE_SESSIONS_REF).document(session.id)
            .set(session)
            .await()
    }

    override fun getPendingSingleSessionRequests(teacherId: String): Flow<List<SingleSession>> =
        callbackFlow {
            val listener = firestore.collection(SINGLE_SESSIONS_REF)
                .whereEqualTo("teacherId", teacherId)
                .whereEqualTo("status", SessionStatus.PENDING.name)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val sessions = snapshot?.toObjects(SingleSession::class.java) ?: listOf()
                    trySend(sessions)
                }

            awaitClose { listener.remove() }
        }

    override fun updateSessionStatus(
        sessionId: String,
        status: SessionStatus,
        onResult: (Boolean) -> Unit
    ) {
        firestore.collection(SINGLE_SESSIONS_REF)
            .document(sessionId)
            .update("status", status.name)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    override fun getMyGroupSessions(studentId: String): Flow<List<GroupSession>> = callbackFlow {
        val now = System.currentTimeMillis()
        val listener = firestore.collection(GROUP_SESSIONS_REF)
            .whereArrayContains("students", studentId)
            .whereGreaterThan("startTime", now.toString())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val sessions = snapshot?.toObjects(GroupSession::class.java) ?: listOf()
                trySend(sessions)
            }
        awaitClose { listener.remove() }
    }

    override fun getMySingleSessions(studentId: String): Flow<List<SingleSession>> = callbackFlow {
        val now = System.currentTimeMillis()
        val listener = firestore.collection(SINGLE_SESSIONS_REF)
            .whereEqualTo("studentId", studentId)
            .whereEqualTo("status", SessionStatus.ACCEPTED.name)
            .whereGreaterThan("startTime", now.toString())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val sessions = snapshot?.toObjects(SingleSession::class.java) ?: listOf()
                trySend(sessions)
            }
        awaitClose { listener.remove() }
    }

    override fun getAvailableGroupSessions(studentId: String): Flow<List<GroupSession>> =
        callbackFlow {
            val now = System.currentTimeMillis()
            val listener = firestore.collection(GROUP_SESSIONS_REF)
                .whereGreaterThan("startTime", now.toString())
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    val sessions = snapshot?.toObjects(GroupSession::class.java) ?: listOf()

                    launch {
                        val filteredSessions = sessions.filter { session ->
                            val courseDoc =
                                firestore.collection(COURSES_REF).document(session.courseId).get()
                                    .await()
                            val enrolledStudents =
                                courseDoc.get("enrolledStudents") as? List<String> ?: emptyList()
                            enrolledStudents.contains(studentId)
                        }
                        trySend(filteredSessions)
                    }
                }
            awaitClose { listener.remove() }
        }

    override fun getPendingRequests(studentId: String): Flow<List<SingleSession>> = callbackFlow {
        val listener = firestore.collection(SINGLE_SESSIONS_REF)
            .whereEqualTo("studentId", studentId)
//            .whereEqualTo("status", SessionStatus.PENDING.name)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val requests = snapshot?.toObjects(SingleSession::class.java) ?: listOf()
                trySend(requests)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun enrollUserInGroupSession(sessionId: String, userId: String): Result<Unit> = try {
        val docRef = firestore.collection(GROUP_SESSIONS_REF).document(sessionId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val currentList = snapshot.get("students") as? List<String> ?: emptyList()
            if (!currentList.contains(userId)) {
                val updatedList = currentList + userId
                transaction.update(docRef, "students", updatedList)
            }
        }.await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }


}
