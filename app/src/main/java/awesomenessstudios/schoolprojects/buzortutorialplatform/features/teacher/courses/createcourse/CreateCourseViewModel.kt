package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.courses.createcourse

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Course
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.CourseSection
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.GroupSession
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Constants
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Constants.COURSES_REF
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Constants.GROUP_SESSIONS_REF
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.OpenAIService
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CreateCourseViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val openAIService: OpenAIService,
    private val userPreferences: UserPreferences

) : ViewModel() {

    // State for the course creation flow
    private val _state = mutableStateOf(CreateCourseState())
    val state: State<CreateCourseState> = _state

    // Step indicator for the flow
    private val _currentStep = MutableStateFlow(1)
    val currentStep: StateFlow<Int> = _currentStep

    init {
        viewModelScope.launch {

                _state.value = _state.value.copy(
                    loggedInUser = auth.currentUser!!.uid
                )

        }
    }


    // Handle events from the UI
    fun onEvent(event: CreateCourseEvent) {
        when (event) {
            is CreateCourseEvent.SubjectChanged -> {
                _state.value = _state.value.copy(subject = event.subject)
            }

            is CreateCourseEvent.TargetGradesChanged -> {
                val updatedGrades = _state.value.targetGrades.toMutableList()
                if (event.grade in updatedGrades) {
                    updatedGrades.remove(event.grade)
                } else {
                    updatedGrades.add(event.grade)
                }
                _state.value = _state.value.copy(targetGrades = updatedGrades)
            }

            is CreateCourseEvent.TitleChanged -> {
                _state.value = _state.value.copy(title = event.title)
            }

            is CreateCourseEvent.DescriptionChanged -> {
                _state.value = _state.value.copy(description = event.description)
            }

            is CreateCourseEvent.PriceChanged -> {
                _state.value = _state.value.copy(price = event.price)
            }

            is CreateCourseEvent.CoverImageChanged -> {
                _state.value = _state.value.copy(coverImage = event.uri)
            }

            CreateCourseEvent.AddSection -> {
                val updatedSections = _state.value.sections.toMutableList()
                if (updatedSections.size < 3) {
                    updatedSections.add(CourseSection())
                    _state.value = _state.value.copy(sections = updatedSections)
                }
            }

            is CreateCourseEvent.SectionTitleChanged -> {
                val updatedSections = _state.value.sections.toMutableList()
                updatedSections[event.index] =
                    updatedSections[event.index].copy(title = event.title)
                _state.value = _state.value.copy(sections = updatedSections)
            }

            is CreateCourseEvent.SectionMaterialChanged -> {
                val updatedSections = _state.value.sections.toMutableList()
                updatedSections[event.index] =
                    updatedSections[event.index].copy(material = event.materialUri)
                _state.value = _state.value.copy(sections = updatedSections)
            }

            is CreateCourseEvent.SectionFootnoteChanged -> {
                val updatedSections = _state.value.sections.toMutableList()
                updatedSections[event.index] =
                    updatedSections[event.index].copy(footnote = event.footnote)
                _state.value = _state.value.copy(sections = updatedSections)
            }

            is CreateCourseEvent.RemoveSection -> {
                val updatedSections = _state.value.sections.toMutableList()
                updatedSections.removeAt(event.index)
                _state.value = _state.value.copy(sections = updatedSections)
            }

            is CreateCourseEvent.PrivateSessionChanged -> {
                _state.value = _state.value.copy(allowPrivateSessions = event.allow)
            }

            is CreateCourseEvent.PrivateSessionPriceChanged -> {
                _state.value = _state.value.copy(privateSessionPrice = event.price)
            }

            is CreateCourseEvent.GroupSessionChanged -> {
                _state.value = _state.value.copy(allowGroupSessions = event.allow)
            }

            is CreateCourseEvent.GroupSessionPriceChanged -> {
                _state.value = _state.value.copy(groupSessionPrice = event.price)
            }

            CreateCourseEvent.IncreaseMaxSeats -> {
                _state.value = _state.value.copy(maxSeats = _state.value.maxSeats + 1)
            }

            CreateCourseEvent.DecreaseMaxSeats -> {
                if (_state.value.maxSeats > 0) {
                    _state.value = _state.value.copy(maxSeats = _state.value.maxSeats - 1)
                }
            }

            CreateCourseEvent.NextStep -> {
                _currentStep.value = _currentStep.value + 1
            }

            CreateCourseEvent.CreateCourse -> {
                createCourse()
            }

            is CreateCourseEvent.GroupSessionDateChanged -> {
                _state.value = _state.value.copy(sessionDate = event.date)
            }
        }
    }

    // Generate a short description using AI (mock implementation)
    fun generateDescription() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            try {
                val description = openAIService.generateCourseDescription(
                    subject = _state.value.subject,
                    targetGrades = _state.value.targetGrades,
                    title = _state.value.title
                )
                _state.value = _state.value.copy(description = description)
            } catch (e: Exception) {
                _state.value =
                    _state.value.copy(errorMessage = "Failed to generate description: ${e.message}")
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    // Upload a file to Firebase Storage and return the download URL
    private suspend fun uploadFile(uri: Uri, path: String): String {
        val storageRef = storage.reference.child(path)
        val uploadTask = storageRef.putFile(uri).await()
        return storageRef.downloadUrl.await().toString()
    }

    // Create a Zoom meeting link (mock implementation)
    private suspend fun createZoomMeeting(): String {
        // Replace with actual Zoom API integration
        return Constants.ZOOM_LINK
    }

    // Save the course to Firestore
    private fun createCourse() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            try {
                // Upload cover image to Firebase Storage
                val coverImageUrl = if (_state.value.coverImage.isNotEmpty()) {
                    uploadFile(
                        Uri.parse(_state.value.coverImage),
                        "courses/${_state.value.title}/cover.jpg"
                    )
                } else {
                    ""
                }

                // Upload section materials to Firebase Storage
                val sections = _state.value.sections.map { section ->
                    val materialUrl = if (section.material.isNotEmpty()) {
                        uploadFile(
                            Uri.parse(section.material),
                            "courses/${_state.value.title}/${section.title}.pdf"
                        )
                    } else {
                        ""
                    }
                    section.copy(material = materialUrl)
                }

                // Create a Zoom meeting link for group sessions
                val zoomLink = if (_state.value.allowGroupSessions) {
                    createZoomMeeting()
                } else {
                    ""
                }

                // Save course to Firestore
                val course = Course(
                    id = db.collection(COURSES_REF).document().id,
                    title = _state.value.title,
                    ownerId = _state.value.loggedInUser.takeIf { !it.isNullOrBlank() }
                        ?: FirebaseAuth.getInstance().currentUser?.uid.orEmpty(),
                    price = _state.value.price,
                    subject = _state.value.subject,
                    targetGrades = _state.value.targetGrades,
                    coverImage = coverImageUrl,
                    description = _state.value.description,
                    allowPrivateSessions = _state.value.allowPrivateSessions,
                    privateSessionPrice = _state.value.privateSessionPrice,
                    courseNoteOneTitle = sections.getOrNull(0)?.title ?: "",
                    courseNoteOne = sections.getOrNull(0)?.material ?: "",
                    courseNoteOneFootnote = sections.getOrNull(0)?.footnote ?: "",
                    courseNoteTwoTitle = sections.getOrNull(1)?.title ?: "",
                    courseNoteTwo = sections.getOrNull(1)?.material ?: "",
                    courseNoteTwoFootnote = sections.getOrNull(1)?.footnote ?: "",
                    courseNoteThreeTitle = sections.getOrNull(2)?.title ?: "",
                    courseNoteThree = sections.getOrNull(2)?.material ?: "",
                    courseNoteThreeFootnote = sections.getOrNull(2)?.footnote ?: "",
                    dateCreated = SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()
                    ).format(Date())
                )

                db.collection(COURSES_REF).document(course.id).set(course).await()

                // Save group session to Firestore if enabled
                if (_state.value.allowGroupSessions) {
                    val groupSession = GroupSession(
                        id = db.collection(GROUP_SESSIONS_REF).document().id,
                        courseId = course.id,
                        teacherId = _state.value.loggedInUser,
                        students = listOf(),
                        startTime = "${_state.value.sessionDate} ${_state.value.sessionTime}",
                        price = _state.value.groupSessionPrice,
                        type = "Group",
                        sessionLink = zoomLink,
                        maxAttendance = _state.value.maxSeats,
                        dateCreated = SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss",
                            Locale.getDefault()
                        ).format(Date())
                    )
                    db.collection(GROUP_SESSIONS_REF).document(groupSession.id)
                        .set(groupSession).await()
                }

                _state.value = _state.value.copy(isLoading = false, isCourseCreated = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to create course"
                )
            }
        }
    }
}