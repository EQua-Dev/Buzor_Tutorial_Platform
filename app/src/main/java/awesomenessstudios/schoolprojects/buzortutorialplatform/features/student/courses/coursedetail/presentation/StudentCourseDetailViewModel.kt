package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.courses.coursedetail.presentation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.Result
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.enums.SessionStatus
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Course
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.GroupSession
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.SingleSession
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Teacher
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Wallet
import awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.coursesrepo.CourseRepository
import awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.sessionrepo.SessionRepository
import awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.teacherrepo.TeacherRepository
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
class StudentCourseDetailViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val courseRepository: CourseRepository,
    private val teacherRepository: TeacherRepository,
    private val walletRepository: WalletRepository,
    private val sessionRepository: SessionRepository,
    private val locationUtils: LocationUtils,
) : ViewModel() {

    var courseState by mutableStateOf<Course?>(null)
        private set

    var teacherState by mutableStateOf<Teacher?>(null)
        private set

    var groupSessionsState by mutableStateOf<List<GroupSession>?>(null)
        private set

    var loadingState by mutableStateOf<Boolean>(false)
        private set

    var selectedTab by mutableStateOf("Content")
        private set

    var walletState by mutableStateOf<Wallet?>(null)
        private set

    var showFundingDialog by mutableStateOf(false)
        private set
    var showRequestDialog by mutableStateOf(false)
        private set

    var newSessionData by mutableStateOf(NewSingleSessionData())
        private set

    fun onRequestSessionClicked() {
        showRequestDialog = true
    }

    fun onDismissDialog() {
        showRequestDialog = false
        newSessionData = NewSingleSessionData() // reset
    }

    fun onUpdateNewSessionData(updatedData: NewSingleSessionData) {
        newSessionData = updatedData
    }

    fun loadCourse(courseId: String) {
        viewModelScope.launch {
            loadingState = true
            courseRepository.getCourseByIdRealtime(courseId).collect { course ->
                courseState = course
                loadTeacher(course.ownerId)
                loadingState = false
            }
        }
    }

    private fun loadTeacher(ownerId: String) {
        viewModelScope.launch {
            teacherRepository.getTeacherDetailsById(ownerId).collect { teacher ->
                teacherState = teacher
            }
        }
    }

    fun loadSessions(courseId: String) {
        viewModelScope.launch {
            loadingState = true
            sessionRepository.getUpcomingGroupSessionsForCourse(courseId).collect { groupSessions ->
                Log.d("TAG", "loadSessions: $groupSessions")
                groupSessionsState = groupSessions
            }
            loadingState = false

        }
    }

    fun onTabSelected(tab: String) {
        selectedTab = tab
    }


    @RequiresApi(Build.VERSION_CODES.P)
    fun createSingleSession(
        courseId: String,
        teacherId: String,
        activity: FragmentActivity,
        amount: Double
    ) {
        try {
            // 1. Get current location for debit transaction
            val location = locationUtils.getCurrentLocation()

            locationUtils.getCurrentLocation()
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val locationAddress = locationUtils.getLocationAddress(location)

                        // 2. Debit student's wallet
                        viewModelScope.launch {
                            walletRepository.debitWallet(
                                userId = auth.currentUser!!.uid,
                                amount = amount,
                                description = "Private session payment for: ${courseState?.title}",
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
                            sessionType = "Single",
                            courseId = courseId,
//                            sessionId = sess
                        ).getOrThrow()
                    }
                }
//

            }

            // 4. Enroll student in course
            HelpMe.promptBiometric(
                activity = activity,
                title = "Authorize Payment for ${courseState?.title} private session",
                onSuccess = {
                    viewModelScope.launch {
                        sessionRepository.createSingleSession(
                            SingleSession(
                                id = UUID.randomUUID().toString(),
                                courseId = courseId,
                                teacherId = teacherId,
                                studentId = auth.currentUser!!.uid,
                                startTime = newSessionData.startTime,
                                type = "Single",
                                sessionLink = Constants.ZOOM_LINK, // optional
                                price = courseState!!.privateSessionPrice,
                                dateCreated = System.currentTimeMillis().toString(),
                                status = SessionStatus.PENDING.name
                            )
                        )
                        onDismissDialog()
                    }
                },
                onNoHardware = {
                    viewModelScope.launch {
                        sessionRepository.createSingleSession(
                            SingleSession(
                                courseId = courseId,
                                teacherId = teacherId,
                                studentId = auth.currentUser!!.uid,
                                startTime = newSessionData.startTime,
                                type = "Single",
                                sessionLink = Constants.ZOOM_LINK, // optional
                                price = newSessionData.price,
                                dateCreated = System.currentTimeMillis().toString(),
                                status = SessionStatus.PENDING.name
                            )
                        )
                        onDismissDialog()
                    }
                }
            )


            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }

    }

    fun checkWalletAndProceed(
        userId: String,
        amount: Double?,
        onSufficientFunds: () -> Unit,
        onInsufficientFunds: @Composable () -> Unit
    ) {
        viewModelScope.launch {
            val wallet = walletRepository.getWalletByUserId(userId)
            walletState = wallet
            val coursePrice = amount ?: courseState?.price?.toDoubleOrNull() ?: 0.0
            if (wallet != null && wallet.balance.toDouble() >= coursePrice) {
                onSufficientFunds()
            } else {
                showFundingDialog = true
//                onInsufficientFunds()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    suspend fun enrollInCourse(activity: FragmentActivity, userId: String): Result<Unit> {
        val course = courseState ?: return Result.Failure(Exception("Course not loaded"))
        val coursePrice = course.price.toDoubleOrNull() ?: 0.0
        val teacherId = course.ownerId

        return try {
            // 1. Get current location for debit transaction
            val location = locationUtils.getCurrentLocation()

            locationUtils.getCurrentLocation()
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val locationAddress = locationUtils.getLocationAddress(location)

                        // 2. Debit student's wallet
                        viewModelScope.launch {
                            walletRepository.debitWallet(
                                userId = userId,
                                amount = coursePrice,
                                description = "Course enrollment: ${course.title}",
                                location = locationAddress,
                                receiver = course.ownerId
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


            // 3. Credit teacher's wallet

            walletRepository.creditWallet(
                userId = teacherId,
                amount = coursePrice,
                description = "Course sale: ${course.title}",
                sender = userId
            ).getOrThrow()


            // 4. Enroll student in course
            HelpMe.promptBiometric(
                activity = activity,
                title = "Authorize Payment for ${course.title}",
                onSuccess = {
                    viewModelScope.launch {
                        courseRepository.enrollUserInCourse(course.id, userId).getOrThrow()
                    }
                },
                onNoHardware = {
                    viewModelScope.launch {
                        courseRepository.enrollUserInCourse(course.id, userId).getOrThrow()
                    }
                }
            )


            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun rateCourse(courseId: String, userId: String, rating: Int, activity: FragmentActivity, courseTitle: String) {
        HelpMe.promptBiometric(
            activity = activity,
            title = "Confirm rating $courseTitle $rating stars",
            onSuccess = {
                viewModelScope.launch {
                    val result = courseRepository.rateCourse(courseId, userId, rating)
                    if (result.isSuccess) {
                        // Optionally trigger UI state update or refresh course data
                        Log.d("CourseViewModel", "Rating submitted successfully.")
                    } else {
                        Log.e("CourseViewModel", "Failed to rate course", result.exceptionOrNull())
                    }
                }
            },
            onNoHardware = {
                viewModelScope.launch {
                    val result = courseRepository.rateCourse(courseId, userId, rating)
                    if (result.isSuccess) {
                        // Optionally trigger UI state update or refresh course data
                        Log.d("CourseViewModel", "Rating submitted successfully.")
                    } else {
                        Log.e("CourseViewModel", "Failed to rate course", result.exceptionOrNull())
                    }
                }
            }
        )


    }



    fun dismissFundingDialog() {
        showFundingDialog = false
    }
}


data class NewSingleSessionData(
    val startTime: String = "",
    val type: String = "",
    val price: String = ""
)