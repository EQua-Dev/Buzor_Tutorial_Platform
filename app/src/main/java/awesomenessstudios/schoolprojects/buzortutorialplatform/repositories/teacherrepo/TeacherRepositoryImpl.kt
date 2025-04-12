package awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.teacherrepo

import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Teacher
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Constants.TEACHERS_REF
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class TeacherRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : TeacherRepository {
    override fun getTeacherDetailsById(ownerId: String): Flow<Teacher> = callbackFlow {
        val subscription = firestore.collection(TEACHERS_REF)
            .document(ownerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    close(error)
                    return@addSnapshotListener
                }
                val teacher = snapshot.toObject(Teacher::class.java) ?: Teacher()
                trySend(teacher)
            }
        awaitClose { subscription.remove() }
    }
}