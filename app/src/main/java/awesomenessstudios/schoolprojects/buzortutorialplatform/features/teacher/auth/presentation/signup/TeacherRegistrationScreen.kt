package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.auth.presentation.signup

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import awesomenessstudios.schoolprojects.buzortutorialplatform.R
import awesomenessstudios.schoolprojects.buzortutorialplatform.components.AssimOutlinedDropdown
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.auth.presentation.signup.StudentRegistrationEvent
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.auth.presentation.signup.StudentRegistrationViewModel
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Constants.subjects

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherRegistrationScreen(
    viewModel: TeacherRegistrationViewModel = hiltViewModel(),
    onRegistrationSuccess: () -> Unit
) {
    val state = viewModel.state

    val context = LocalContext.current
    val activity = context as? Activity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "Teacher Registration", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center,)


        // First Name
        OutlinedTextField(
            value = state.value.firstName,
            onValueChange = { viewModel.onEvent(TeacherRegistrationEvent.FirstNameChanged(it)) },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Last Name
        OutlinedTextField(
            value = state.value.lastName,
            onValueChange = { viewModel.onEvent(TeacherRegistrationEvent.LastNameChanged(it)) },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email
        OutlinedTextField(
            value = state.value.email,
            onValueChange = { viewModel.onEvent(TeacherRegistrationEvent.EmailChanged(it)) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Password
        var passwordVisible by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = state.value.password,
            onValueChange = { viewModel.onEvent(TeacherRegistrationEvent.PasswordChanged(it)) },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Default.Visibility
                else
                    Icons.Default.VisibilityOff

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = image,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            }
        )


        Spacer(modifier = Modifier.height(8.dp))

        // Phone Number
        OutlinedTextField(
            value = state.value.phoneNumber,
            onValueChange = { viewModel.onEvent(TeacherRegistrationEvent.PhoneNumberChanged(it)) },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subjects Dropdown
        AssimOutlinedDropdown(
            label = stringResource(id = R.string.subjects_label),
            hint = stringResource(id = R.string.subjects_hint),
            options = subjects,
            selectedValue = state.value.subjects.joinToString(", "),
            onValueSelected = { viewModel.onEvent(TeacherRegistrationEvent.SubjectsChanged(it.toString())) },
            isCompulsory = true,
//            error = state.genderError?.let { stringResource(id = it) },
            isSearchable = true // Enable search functionality
        )
       /* var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = state.value.subjects.joinToString(", "),
                onValueChange = {},
                label = { Text("Subjects") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                subjects.forEach { subject ->
                    DropdownMenuItem(
                        text = {
                            Text(text = subject)
                        },
                        onClick = {
                            viewModel.onEvent(TeacherRegistrationEvent.SubjectsChanged(subject))
                        },
                        modifier = Modifier.background(color = Color.Red)
                    )
                }
            }
        }*/

        Spacer(modifier = Modifier.height(16.dp))

        // Register Button
        Button(
            onClick = { viewModel.onEvent(TeacherRegistrationEvent.Register(activity!!)) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
        if (state.value.isOtpSent) {
            // OTP Input Field
            OutlinedTextField(
                value = state.value.otp,
                onValueChange = { viewModel.onEvent(TeacherRegistrationEvent.OtpChanged(it)) },
                label = { Text("Enter OTP") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Verify Button
            Button(
                onClick = { viewModel.onEvent(TeacherRegistrationEvent.VerifyOtp) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Verify OTP")
            }
        }

        // Show loading or error messages
        if (state.value.isLoading) {
            CircularProgressIndicator()
        }

        state.value.errorMessage?.let { error ->
            Text(text = error, color = Color.Red)
        }

        // Navigate on success
        LaunchedEffect(state.value.isRegistrationSuccessful) {
            if (state.value.isRegistrationSuccessful) {
                onRegistrationSuccess()
            }
        }
    }
}