package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.sessions.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import awesomenessstudios.schoolprojects.buzortutorialplatform.repositories.sessionrepo.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherSessionViewModel @Inject constructor(
    private val repository: SessionRepository
) : ViewModel() {

    var state by mutableStateOf(TeacherSessionState())
        private set

    fun loadSessions(teacherId: String) {
        viewModelScope.launch {
            launch {
                repository.getUpcomingGroupSessions(teacherId).collect {
                    state = state.copy(groupSessions = it)
                }
            }
            launch {
                repository.getUpcomingSingleSessions(teacherId).collect {
                    state = state.copy(singleSessions = it)
                }
            }
        }
    }
}
