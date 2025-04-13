package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherHomeViewModel @Inject constructor(private val userPreferences: UserPreferences) :
    ViewModel() {

    fun logOut(onSuccess: () -> Unit) {
        viewModelScope.launch {
            userPreferences.clearUserId()
            onSuccess()
        }
    }

}