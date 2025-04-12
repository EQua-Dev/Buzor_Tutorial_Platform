package awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.teacherrepo

import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Teacher
import kotlinx.coroutines.flow.Flow

interface TeacherRepository {
    fun getTeacherDetailsById(ownerId: String): Flow<Teacher>

}