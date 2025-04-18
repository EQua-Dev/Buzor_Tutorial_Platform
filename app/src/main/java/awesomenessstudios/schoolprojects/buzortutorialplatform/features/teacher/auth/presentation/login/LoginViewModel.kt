package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.auth.presentation.login

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Common.mAuth
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val userPreferences: UserPreferences): ViewModel() {
    private val _state = mutableStateOf(LoginState())
    val state: State<LoginState> = _state

    init {
        viewModelScope.launch {
            userPreferences.role.collect { role ->
                Log.d("LVM", "role: $role")
                if (role != null) {
                    _state.value = _state.value.copy(
                        userRole = role.name
                    )
                }
            }
        }
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                _state.value = _state.value.copy(email = event.email)
            }
            is LoginEvent.PasswordChanged -> {
                _state.value = _state.value.copy(password = event.password)
            }
            LoginEvent.Login -> {
                loginUser()
            }
        }
    }

    private fun loginUser() {
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)

        val email = _state.value.email
        val password = _state.value.password

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isLoginSuccessful = true
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = task.exception?.message ?: "Login failed"
                    )
                }
            }
    }
}