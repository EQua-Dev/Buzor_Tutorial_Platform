package awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.studentrepo

import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Student
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Constants
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Constants.STUDENTS_REF
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StudentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : StudentRepository {

    override suspend fun getStudentProfile(studentId: String): Student {
        return firestore.collection(STUDENTS_REF)
            .document(studentId)
            .get()
            .await()
            .toObject(Student::class.java) ?: Student()
    }

    override suspend fun updateStudentProfile(student: Student) {
        firestore.collection(STUDENTS_REF)
            .document(student.id)
            .set(student)
            .await()
    }
}
