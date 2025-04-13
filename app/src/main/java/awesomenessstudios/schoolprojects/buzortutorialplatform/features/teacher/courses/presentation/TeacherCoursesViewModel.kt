package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.courses.presentation

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.coursesrepo.CourseRepository
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TeacherCoursesViewModel @Inject constructor(
    private val repository: CourseRepository,
    private val userPreferences: UserPreferences,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _state = MutableStateFlow(TeacherCoursesState())
    val state: StateFlow<TeacherCoursesState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<TeacherCoursesEvent>()
    val events: SharedFlow<TeacherCoursesEvent> = _events.asSharedFlow()

    private val _loggedInUser = mutableStateOf("")
    val loggedInUser: State<String> = _loggedInUser

    init {
        viewModelScope.launch {
            userPreferences.loggedInUserId.collect { userId ->

                    _loggedInUser.value = userId

            }
        }
        loadCourses()
    }

    fun loadCourses() {
        val currentUser = auth.currentUser
        Log.d("TAG", "loadCourses: $currentUser")
        if (currentUser == null) {
            _state.update { it.copy(error = "User not authenticated") }
            return
        }

        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            repository.getTeacherCourses(currentUser.uid)
                .catch { error ->
                    _state.update { it.copy(error = error.message, isLoading = false) }
                }
                .collect { courses ->
                    _state.update { it.copy(courses = courses, isLoading = false) }
                }
        }
    }

    fun onEvent(event: TeacherCoursesEvent) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }
}