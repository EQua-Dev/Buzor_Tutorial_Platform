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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _courseState = MutableStateFlow<Course?>(null)
    val courseState = _courseState.asStateFlow()

    private val _teacherState = MutableStateFlow<Teacher?>(null)
    val teacherState = _teacherState.asStateFlow()

    private val _groupSessionsState = MutableStateFlow<List<GroupSession>?>(null)
    val groupSessionsState = _groupSessionsState.asStateFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    private val _selectedTab = MutableStateFlow("Content")
    val selectedTab = _selectedTab.asStateFlow()

    private val _walletState = MutableStateFlow<Wallet?>(null)
    var walletState = _walletState.asStateFlow()

    private val _showFundingDialog = MutableStateFlow(false)
    val showFundingDialog = _showFundingDialog.asStateFlow()

    private val _showRequestDialog = MutableStateFlow(false)
    var showRequestDialog = _showRequestDialog.asStateFlow()

    private val _newSessionData = MutableStateFlow(NewSingleSessionData())
    val newSessionData = _newSessionData.asStateFlow()


    fun onRequestSessionClicked() {
        _showRequestDialog.value = true
    }


    fun onDismissDialog() {
        _showRequestDialog.value = false
        _newSessionData.value = NewSingleSessionData() // reset
    }

    fun onUpdateNewSessionData(updatedData: NewSingleSessionData) {
        _newSessionData.value = updatedData
    }

    fun onTabSelected(tab: String) {
        _selectedTab.value = tab
    }

    fun onSetShowFundingDialog(value: Boolean) {
        _showFundingDialog.value = value
    }

    fun onsetShowRequestDialog(value: Boolean) {
        _showRequestDialog.value = value
    }

    fun loadCourse(courseId: String) {
        viewModelScope.launch {
            _loadingState.value = true
            courseRepository.getCourseByIdRealtime(courseId).collect { course ->
                _courseState.value = course
                loadTeacher(course.ownerId)
                _loadingState.value = false
            }
        }
    }


    private fun loadTeacher(ownerId: String) {
        viewModelScope.launch {
            teacherRepository.getTeacherDetailsById(ownerId).collect { teacher ->
                _teacherState.value = teacher
            }
        }
    }

    fun loadSessions(courseId: String) {
        viewModelScope.launch {
            _loadingState.value = true
            sessionRepository.getUpcomingGroupSessionsForCourse(courseId).collect { groupSessions ->
                Log.d("TAG", "loadSessions: $groupSessions")
                _groupSessionsState.value = groupSessions
            }
            _loadingState.value = false

        }
    }

    /*
        fun onTabSelected(tab: String) {
            selectedTab = tab
        }
    */


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
                                description = "Private session payment for: ${_courseState.value!!.title}",
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
                title = "Authorize Payment for ${courseState.value!!.title}",
                onSuccess = {

                    val singleSession = SingleSession(
                        courseId = courseId,
                        teacherId = teacherId,
                        startTime = _newSessionData.value.startTime,
                        type = "Single",
                        studentId = auth.currentUser!!.uid,
                        price = amount.toString(),
                        dateCreated = System.currentTimeMillis().toString(),
                        status = SessionStatus.PENDING.name
                    )
                    viewModelScope.launch {
                        sessionRepository.createSingleSession(singleSession)
                    }

//                    createSingleSession(courseId, teacherId, activity, amount)
//                    enrollUserInCourse(courseId, auth.currentUser!!.uid)
                },
                onNoHardware = {
                    val singleSession = SingleSession(
                        courseId = courseId,
                        teacherId = teacherId,
                        startTime = _newSessionData.value.startTime,
                        type = "Single",
                        studentId = auth.currentUser!!.uid,
                        price = amount.toString(),
                        dateCreated = System.currentTimeMillis().toString(),
                        status = SessionStatus.PENDING.name
                    )
                    viewModelScope.launch {
                        sessionRepository.createSingleSession(singleSession)
                    }
                }
            )

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }

    }


    private fun enrollUserInCourse(courseId: String, userId: String) {
        viewModelScope.launch {
            courseRepository.enrollUserInCourse(courseId, userId).getOrThrow()
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
            _walletState.value = wallet
            val coursePrice = amount ?: _courseState.value!!.price.toDoubleOrNull() ?: 0.0
            if (wallet != null && wallet.balance.toDouble() >= coursePrice) {
                onSufficientFunds()
            } else {
                _showFundingDialog.value = true
//                onInsufficientFunds()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    suspend fun enrollInCourse(activity: FragmentActivity, userId: String): Result<Unit> {
        val course = _courseState ?: return Result.Failure(Exception("Course not loaded"))
        val coursePrice = _courseState.value!!.price.toDoubleOrNull() ?: 0.0
        val teacherId = _courseState.value!!.ownerId

        return try {
            // 1. Get current location for debit transaction
            val location = locationUtils.getCurrentLocation()


            // 4. Enroll student in course
            HelpMe.promptBiometric(
                activity = activity,
                title = "Authorize Payment for ${_courseState.value!!.title}",
                onSuccess = {
                    viewModelScope.launch {
                        locationUtils.getCurrentLocation()
                            .addOnSuccessListener { location ->
                                if (location != null) {
                                    val locationAddress = locationUtils.getLocationAddress(location)

                                    // 2. Debit student's wallet
                                    viewModelScope.launch {
                                        walletRepository.debitWallet(
                                            userId = userId,
                                            amount = coursePrice,
                                            description = "Course enrollment: ${_courseState.value!!.title}",
                                            location = locationAddress,
                                            receiver = _courseState.value!!.ownerId
                                        ).getOrThrow()

                                        // 3. Credit teacher's wallet

                                        walletRepository.creditWallet(
                                            userId = teacherId,
                                            amount = coursePrice,
                                            description = "Course sale: ${_courseState.value!!.title}",
                                            sender = userId
                                        ).getOrThrow()


                                        courseRepository.enrollUserInCourse(
                                            _courseState.value!!.id,
                                            userId
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


                    }
                },
                onNoHardware = {
                    viewModelScope.launch {
                        locationUtils.getCurrentLocation()
                            .addOnSuccessListener { location ->
                                if (location != null) {
                                    val locationAddress = locationUtils.getLocationAddress(location)

                                    // 2. Debit student's wallet
                                    viewModelScope.launch {
                                        walletRepository.debitWallet(
                                            userId = userId,
                                            amount = coursePrice,
                                            description = "Course enrollment: ${_courseState.value!!.title}",
                                            location = locationAddress,
                                            receiver = _courseState.value!!.ownerId
                                        ).getOrThrow()

                                        // 3. Credit teacher's wallet

                                        walletRepository.creditWallet(
                                            userId = teacherId,
                                            amount = coursePrice,
                                            description = "Course sale: ${_courseState.value!!.title}",
                                            sender = userId
                                        ).getOrThrow()


                                        courseRepository.enrollUserInCourse(
                                            _courseState.value!!.id,
                                            userId
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


                    }
                }
            )


            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun rateCourse(
        courseId: String,
        userId: String,
        rating: Int,
        activity: FragmentActivity,
        courseTitle: String
    ) {
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
        _showFundingDialog.value = false
    }
}


data class NewSingleSessionData(
    val startTime: String = "",
    val type: String = "",
    val price: String = ""
)