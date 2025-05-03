package awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.coursesrepo

import android.util.Log
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Course
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Constants.COURSES_REF
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
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

    override fun getCourseByIdRealtime(courseId: String): Flow<Course> = callbackFlow {
        val subscription = firestore.collection(COURSES_REF)
            .document(courseId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    close(error)
                    return@addSnapshotListener
                }
                val course = snapshot.toObject(Course::class.java)
                if (course != null) trySend(course)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun enrollUserInCourse(courseId: String, userId: String): Result<Unit> = try {
        val docRef = firestore.collection(COURSES_REF).document(courseId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val currentList = snapshot.get("enrolledStudents") as? List<String> ?: emptyList()
            if (!currentList.contains(userId)) {
                val updatedList = currentList + userId
                transaction.update(docRef, "enrolledStudents", updatedList)
            }
        }.await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
    override suspend fun rateCourse(courseId: String, userId: String, newRating: Int): Result<Unit> = try {
        val docRef = firestore.collection(COURSES_REF).document(courseId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val raters = snapshot.get("raters") as? Map<String, Long> ?: emptyMap()

            // Update the user's rating
            val updatedRaters = raters.toMutableMap().apply {
                put(userId, newRating.toLong())
            }

            // Calculate the average
            val averageRating = updatedRaters.values.map { it.toDouble() }.average()

            // Push the updates
            transaction.update(docRef, mapOf(
                "raters" to updatedRaters,
                "rating" to averageRating
            ))
        }.await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

}