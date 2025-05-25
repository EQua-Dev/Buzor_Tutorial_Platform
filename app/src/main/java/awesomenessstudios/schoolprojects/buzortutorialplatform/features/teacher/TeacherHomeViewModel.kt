package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.teacherrepo.TeacherRepository
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherHomeViewModel @Inject constructor(
    private val userPreferences: UserPreferences, private val repository: TeacherRepository,
    private val auth: FirebaseAuth
) :
    ViewModel() {


    var state by mutableStateOf(TeacherProfileState())
        private set


    init {
        loadProfile()
    }


    fun logOut(onSuccess: () -> Unit) {
        viewModelScope.launch {
            userPreferences.clearUserId()
            onSuccess()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                val teacherId = auth.currentUser!!.uid
                repository.getTeacherDetailsById(teacherId).collect { teacher ->
                    state = state.copy(teacher = teacher, isLoading = false)
                }

            } catch (e: Exception) {
                state = state.copy(errorMessage = e.localizedMessage, isLoading = false)
            }
        }
    }


}