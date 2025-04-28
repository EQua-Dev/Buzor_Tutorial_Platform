package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.profile.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.studentrepo.StudentRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentProfileViewModel @Inject constructor(
    private val repository: StudentRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    var state by mutableStateOf(StudentProfileState())
        private set

    fun onEvent(event: StudentProfileEvent) {
        when (event) {
            is StudentProfileEvent.FirstNameChanged -> {
                state = state.copy(student = state.student.copy(firstName = event.value))
            }
            is StudentProfileEvent.LastNameChanged -> {
                state = state.copy(student = state.student.copy(lastName = event.value))
            }
            is StudentProfileEvent.PhoneNumberChanged -> {
                state = state.copy(student = state.student.copy(phoneNumber = event.value))
            }
            is StudentProfileEvent.GradeChanged -> {
                state = state.copy(student = state.student.copy(grade = event.value))
            }
            is StudentProfileEvent.PreferredSubjectsChanged -> {
                state = state.copy(student = state.student.copy(preferredSubjects = event.subjects))
            }
            is StudentProfileEvent.ProfileImageChanged -> {
                state = state.copy(student = state.student.copy(profileImage = event.url))
            }
            StudentProfileEvent.ToggleEditMode -> {
                state = state.copy(isEditMode = !state.isEditMode)
            }
            StudentProfileEvent.LoadProfile -> {
                loadProfile()
            }
            StudentProfileEvent.SaveProfile -> {
                saveProfile()
            }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                val studentId = auth.currentUser!!.uid
                val student = repository.getStudentProfile(studentId)
                state = state.copy(student = student, isLoading = false)
            } catch (e: Exception) {
                state = state.copy(errorMessage = e.localizedMessage, isLoading = false)
            }
        }
    }

    private fun saveProfile() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                repository.updateStudentProfile(state.student)
                state = state.copy(isLoading = false, isEditMode = false, isUpdateSuccessful = true)
            } catch (e: Exception) {
                state = state.copy(errorMessage = e.localizedMessage, isLoading = false)
            }
        }
    }
}
