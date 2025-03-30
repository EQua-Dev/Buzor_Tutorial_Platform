package awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.coursesrepo

import android.util.Log
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Course
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Constants.COURSES_REF
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class CourseRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CourseRepository {
    override fun getTeacherCourses(userId: String): Flow<List<Course>> = callbackFlow {

        Log.d("CourseRepo", "getTeacherCourses: $userId")
        val subscription = firestore.collection(COURSES_REF)
            .whereEqualTo("ownerId", userId)
            .whereEqualTo("deleted", false)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val courses = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Course::class.java)?.copy(id = document.id)
                } ?: emptyList()

                trySend(courses)
            }

        awaitClose { subscription.remove() }
    }
}