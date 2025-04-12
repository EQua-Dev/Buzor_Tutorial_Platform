package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.courses.coursedetail.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.Result
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Course
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Teacher
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Wallet
import awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.coursesrepo.CourseRepository
import awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.teacherrepo.TeacherRepository
import awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.walletrepo.WalletRepository
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.HelpMe
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.LocationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentCourseDetailViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val teacherRepository: TeacherRepository,
    private val walletRepository: WalletRepository,
    private val locationUtils: LocationUtils,
) : ViewModel() {

    var courseState by mutableStateOf<Course?>(null)
        private set

    var teacherState by mutableStateOf<Teacher?>(null)
        private set

    var selectedTab by mutableStateOf("Content")
        private set

    var walletState by mutableStateOf<Wallet?>(null)
        private set

    var showFundingDialog by mutableStateOf(false)
        private set


    fun loadCourse(courseId: String) {
        viewModelScope.launch {
            courseRepository.getCourseByIdRealtime(courseId).collect { course ->
                courseState = course
                loadTeacher(course.ownerId)
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

    fun onTabSelected(tab: String) {
        selectedTab = tab
    }

    fun checkWalletAndProceed(
        userId: String,
        onSufficientFunds: () -> Unit,
        onInsufficientFunds: @Composable () -> Unit
    ) {
        viewModelScope.launch {
            val wallet = walletRepository.getWalletByUserId(userId)
            walletState = wallet
            val coursePrice = courseState?.price?.toDoubleOrNull() ?: 0.0
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


    fun dismissFundingDialog() {
        showFundingDialog = false
    }
}