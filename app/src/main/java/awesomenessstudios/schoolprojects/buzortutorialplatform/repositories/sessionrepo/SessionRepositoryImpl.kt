package awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.sessionrepo

import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.GroupSession
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.SingleSession
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Constants.GROUP_SESSIONS_REF
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Constants.SINGLE_SESSIONS_REF
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : SessionRepository {

    override fun getUpcomingGroupSessions(teacherId: String): Flow<List<GroupSession>> = callbackFlow {
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

    override fun getUpcomingSingleSessions(teacherId: String): Flow<List<SingleSession>> = callbackFlow {
        val now = System.currentTimeMillis()
        val listener = firestore.collection(SINGLE_SESSIONS_REF)
            .whereEqualTo("teacherId", teacherId)
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
}
