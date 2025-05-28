package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.sessions.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.Result
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.enums.SessionStatus
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.SingleSession
import awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.coursesrepo.CourseRepository
import awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.sessionrepo.SessionRepository
import awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.walletrepo.WalletRepository
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Constants
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.HelpMe
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.LocationUtils
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class StudentSessionViewModel @Inject constructor(
    private val repository: SessionRepository,
    private val courseRepository: CourseRepository,
    private val walletRepository: WalletRepository,
    private val locationUtils: LocationUtils,
    private val auth: FirebaseAuth,
) : ViewModel() {

    var state by mutableStateOf(StudentSessionState())
        private set

    fun loadStudentSessions(studentId: String) {
        viewModelScope.launch {
            launch {
                repository.getMyGroupSessions(studentId).collect { sessions ->
                    state = state.copy(myGroupSessions = sessions)
                    loadCourseTitles(sessions.map { it.courseId })
                }
            }
            launch {
                repository.getMySingleSessions(studentId).collect { sessions ->
                    state = state.copy(mySingleSessions = sessions)
                    loadCourseTitles(sessions.map { it.courseId })
                }
            }
            launch {
                repository.getAvailableGroupSessions(studentId).collect { sessions ->
                    state = state.copy(availableGroupSessions = sessions)
                    loadCourseTitles(sessions.map { it.courseId })
                }
            }
            launch {
                repository.getPendingRequests(studentId).collect { requests ->
                    state = state.copy(requests = requests)
                    loadCourseTitles(requests.map { it.courseId })
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
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun joinGroupSession(
        courseTitle: String,
        courseId: String,
        sessionId: String,
        teacherId: String,
        studentId: String,
        activity: FragmentActivity,
        amount: Double
    ) {
        try {
            // 1. Get current location for debit transaction
//            val location = locationUtils.getCurrentLocation()

            locationUtils.getCurrentLocation()
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val locationAddress = locationUtils.getLocationAddress(location)

                        // 2. Debit student's wallet
                        viewModelScope.launch {
                            walletRepository.debitWallet(
                                userId = studentId,
                                amount = amount,
                                description = "Group session payment for: $courseTitle",
                                location = locationAddress,
                                receiver = teacherId
                            ).getOrThrow()
                        }
                    } else {
                        /* _state.value = _state.value.copy(
                             isLoading = false,
                             errorMessage = "Unable to fetch location"
                         )*/
                    }
                }
                .addOnFailureListener { e ->
                    /* _state.value = _state.value.copy(
                         isLoading = false,
                         errorMessage = e.message ?: "Failed to get location"
                     )*/
                }


            // 3. Add funds to escrow
            viewModelScope.launch {
                val studentWallet = walletRepository.getWalletByUserId(auth.currentUser!!.uid)
                val teacherWallet = walletRepository.getWalletByUserId(teacherId)

                studentWallet?.id?.let {
                    if (teacherWallet != null) {
                        walletRepository.addToEscrow(
                            amount = amount,
                            studentWalletId = it,
                            teacherWalletId = teacherWallet.id,
                            sessionType = "Group",
                            courseId = courseId,
                            sessionId = sessionId
                        ).getOrThrow()
                    }
                }
//

            }

            // 4. Enroll student in course
            HelpMe.promptBiometric(
                activity = activity,
                title = "Authorize Payment for $courseTitle group session",
                onSuccess = {
                    viewModelScope.launch {

                        repository.enrollUserInGroupSession(sessionId, studentId).getOrThrow()

                    }
                },
                onNoHardware = {
                    viewModelScope.launch {
                        repository.enrollUserInGroupSession(sessionId, studentId).getOrThrow()
                    }
                }
            )


            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }

    }

}
