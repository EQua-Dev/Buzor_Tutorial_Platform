package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.sessions.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.enums.SessionStatus
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.SingleSession
import awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.coursesrepo.CourseRepository
import awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.sessionrepo.SessionRepository
import awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.studentrepo.StudentRepository
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.HelpMe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TeacherSessionViewModel @Inject constructor(
    private val repository: SessionRepository,
    private val studentRepository: StudentRepository,
    private val courseRepository: CourseRepository
) : ViewModel()
{

    var state by mutableStateOf(TeacherSessionState())
        private set

    fun loadSessions(teacherId: String) {
        viewModelScope.launch {
            launch {
                repository.getUpcomingGroupSessions(teacherId).collect { groupSessions ->
                    state = state.copy(groupSessions = groupSessions)
                    loadCourseTitles(groupSessions.map { it.courseId })

                }
            }
            launch {
                repository.getUpcomingSingleSessions(teacherId).collect { singleSessions ->
                    state = state.copy(singleSessions = singleSessions)
                    loadCourseTitles(singleSessions.map { it.courseId })
                }
            }

            launch {
                repository.getPendingSingleSessionRequests(teacherId).collect { sessionRequests ->
                    state = state.copy(sessionRequests = sessionRequests)
                    loadCourseTitles(sessionRequests.map { it.courseId })
                }
            }
        }
    }

    private fun loadCourseTitles(courseIds: List<String>) {
        viewModelScope.launch {
            courseIds.distinct().forEach { courseId ->
                if (!state.courseTitles.containsKey(courseId)) {
                    courseRepository.getCourseByIdRealtime(courseId).collect { course ->
                        course?.let {
                            state = state.copy(
                                courseTitles = state.courseTitles + (courseId to course.title)
                            )
                        }
                    } // You need a function like this

                }
            }
        }
    }

    fun loadStudentData(studentId: String) {
        viewModelScope.launch {
            val student = studentRepository.getStudentProfile(studentId)
            state = state.copy(student = student)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun acceptSession(sessionId: String, activity: FragmentActivity) {
        HelpMe.promptBiometric(
            activity = activity,
            title = "Authorize Acceptance for private session",
            onSuccess = {
                viewModelScope.launch {

                    repository.updateSessionStatus(sessionId, SessionStatus.ACCEPTED) { success ->
//                        _updateResult.value = success
                    }
//                    onDismissDialog()
                }
            },
            onNoHardware = {
                viewModelScope.launch {

                    repository.updateSessionStatus(sessionId, SessionStatus.ACCEPTED) { success ->
//                        _updateResult.value = success
                    }
//                    onDismissDialog()
                }
            }
        )

    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun declineSession(sessionId: String, activity: FragmentActivity) {
        HelpMe.promptBiometric(
            activity = activity,
            title = "Authorize Rejection for private session",
            onSuccess = {
                viewModelScope.launch {

                    repository.updateSessionStatus(sessionId, SessionStatus.ACCEPTED) { success ->
//                        _updateResult.value = success
                    }
//                    onDismissDialog()
                }
            },
            onNoHardware = {
                viewModelScope.launch {

                    repository.updateSessionStatus(sessionId, SessionStatus.DECLINED) { success ->
//                        _updateResult.value = success
                    }
//                    onDismissDialog()
                }
            }
        )
    }
}
