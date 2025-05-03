package awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.coursesrepo

import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Course
import kotlinx.coroutines.flow.Flow

interface CourseRepository {
    fun getTeacherCourses(userId: String): Flow<List<Course>>
    fun getCourseByIdRealtime(courseId: String): Flow<Course>
    suspend fun enrollUserInCourse(courseId: String, userId: String): Result<Unit>
    suspend fun rateCourse(courseId: String, userId: String, newRating: Int): Result<Unit>

}