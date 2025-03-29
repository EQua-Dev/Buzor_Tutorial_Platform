package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.courses.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Course
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class StudentCoursesViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _state = MutableStateFlow(StudentCoursesState())
    val state: StateFlow<StudentCoursesState> = _state.asStateFlow()

    private var studentListener: ListenerRegistration? = null
    private var coursesListener: ListenerRegistration? = null

    init {
        onEvent(StudentCoursesEvent.LoadStudentData)
    }

    fun onEvent(event: StudentCoursesEvent) {
        when (event) {
            is StudentCoursesEvent.LoadStudentData -> loadStudentData()
            is StudentCoursesEvent.CoursesLoaded -> {
                _state.update { it.copy(
                    courses = event.courses,
                    isLoading = false,
                    error = null
                ) }
            }
            is StudentCoursesEvent.ErrorOccurred -> {
                _state.update { it.copy(
                    isLoading = false,
                    error = event.message
                ) }
            }
            is StudentCoursesEvent.StudentDataLoaded -> {
                _state.update { it.copy(
                    studentGrade = event.student.grade
                ) }
                loadCoursesForGrade(event.student.grade)
            }
        }
    }

    private fun loadStudentData() {
        val userId = auth.currentUser?.uid ?: run {
            onEvent(StudentCoursesEvent.ErrorOccurred("User not logged in"))
            return
        }

        studentListener = firestore.collection("Buzor Platform Students").document(userId)
            .addSnapshotListener { snapshot, error ->
                error?.let {
                    onEvent(StudentCoursesEvent.ErrorOccurred(it.message ?: "Error loading student data"))
                    return@addSnapshotListener
                }

                snapshot?.let { doc ->
                    if (doc.exists()) {
                        doc.toObject(Student::class.java)?.let { student ->
                            onEvent(StudentCoursesEvent.StudentDataLoaded(student))
                        } ?: run {
                            onEvent(StudentCoursesEvent.ErrorOccurred("Invalid student data"))
                        }
                    } else {
                        onEvent(StudentCoursesEvent.ErrorOccurred("Student not found"))
                    }
                }
            }
    }

    private fun loadCoursesForGrade(grade: String?) {
        if (grade.isNullOrBlank()) {
            onEvent(StudentCoursesEvent.ErrorOccurred("Student grade not available"))
            return
        }

        coursesListener?.remove()
        coursesListener = firestore.collection("Buzor Courses")
            .whereArrayContains("targetGrades", grade)
            .whereEqualTo("deleted", false)
            .addSnapshotListener { snapshot, error ->
                error?.let {
                    onEvent(StudentCoursesEvent.ErrorOccurred(it.message ?: "Error loading courses"))
                    return@addSnapshotListener
                }

                snapshot?.let { querySnapshot ->
                    val courses = querySnapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(Course::class.java)?.copy(id = doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    onEvent(StudentCoursesEvent.CoursesLoaded(courses))
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        studentListener?.remove()
        coursesListener?.remove()
    }
}