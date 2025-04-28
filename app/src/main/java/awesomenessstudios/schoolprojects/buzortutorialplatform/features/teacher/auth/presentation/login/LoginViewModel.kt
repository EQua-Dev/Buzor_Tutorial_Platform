package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.auth.presentation.login

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.enums.UserRole
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Common
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Common.mAuth
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Constants
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.UserPreferences
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val firestore: FirebaseFirestore
) : ViewModel() {
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
        _state.value =
            _state.value.copy(isLoading = true, errorMessage = null, invalidUserTypeMessage = null)

        val email = _state.value.email
        val password = _state.value.password
        val selectedRole = when (_state.value.userRole) {
            UserRole.STUDENT.name -> Constants.STUDENTS_REF
            UserRole.TEACHER.name -> Constants.TEACHERS_REF
            else -> "" // fallback if none matches
        }


        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = mAuth.currentUser?.uid
                    if (userId != null && _state.value.userRole != null) {
                        firestore.collection(selectedRole)
                            .document(userId)
                            .get()
                            .addOnSuccessListener { documentSnapshot ->
                                if (documentSnapshot.exists()) {
                                    // ✅ User found in the correct role collection
                                    _state.value = _state.value.copy(
                                        isLoading = false,
                                        isLoginSuccessful = true
                                    )
                                } else {
                                    // 🚫 User not found in the selected role
                                    _state.value = _state.value.copy(
                                        isLoading = false,
                                        invalidUserTypeMessage = "This account is not associated with the selected role."
                                    )
                                }
                            }
                            .addOnFailureListener { exception ->
                                _state.value = _state.value.copy(
                                    isLoading = false,
                                    errorMessage = exception.message
                                        ?: "Failed to verify user role."
                                )
                            }
                    } else {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = "User ID or role is missing."
                        )
                    }
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = task.exception?.message ?: "Login failed"
                    )
                }
            }
    }

}