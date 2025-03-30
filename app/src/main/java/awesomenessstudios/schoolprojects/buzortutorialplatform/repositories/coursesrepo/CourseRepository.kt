package awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.coursesrepo

import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Course
import kotlinx.coroutines.flow.Flow

interface CourseRepository {
    fun getTeacherCourses(userId: String): Flow<List<Course>>

}