package awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.studentrepo

import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Student

interface StudentRepository {
    suspend fun getStudentProfile(studentId: String): Student
    suspend fun updateStudentProfile(student: Student)
}
