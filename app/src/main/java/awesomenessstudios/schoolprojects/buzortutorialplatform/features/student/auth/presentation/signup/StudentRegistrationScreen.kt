package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.auth.presentation.signup

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentRegistrationScreen(
    viewModel: StudentRegistrationViewModel = hiltViewModel(),
    onRegistrationSuccess: () -> Unit
) {
    val state = viewModel.state.value

    val context = LocalContext.current
    val activity = context as? Activity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Text(
            text = "Student Registration",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )


        // First Name
        OutlinedTextField(
            value = state.firstName,
            onValueChange = { viewModel.onEvent(StudentRegistrationEvent.FirstNameChanged(it)) },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Last Name
        OutlinedTextField(
            value = state.lastName,
            onValueChange = { viewModel.onEvent(StudentRegistrationEvent.LastNameChanged(it)) },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email
        OutlinedTextField(
            value = state.email,
            onValueChange = { viewModel.onEvent(StudentRegistrationEvent.EmailChanged(it)) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Phone Number
        OutlinedTextField(
            value = state.phoneNumber,
            onValueChange = { viewModel.onEvent(StudentRegistrationEvent.PhoneNumberChanged(it)) },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Password
        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.onEvent(StudentRegistrationEvent.PasswordChanged(it)) },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Grade Dropdown
        var expanded by remember { mutableStateOf(false) }
        val grades = listOf("Grade 1", "Grade 2", "Grade 3", "Grade 4", "Grade 5")

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = state.grade,
                onValueChange = {},
                label = { Text("Grade") },
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
                grades.forEach { grade ->
                    DropdownMenuItem(
                        onClick = {
                            viewModel.onEvent(StudentRegistrationEvent.GradeChanged(grade))
                            expanded = false
                        }, text = { Text(text = grade)}
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Register Button
        Button(
            onClick = { viewModel.onEvent(StudentRegistrationEvent.Register(activity!!)) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        if (state.isOtpSent) {
            // OTP Input Field
            OutlinedTextField(
                value = state.otp,
                onValueChange = { viewModel.onEvent(StudentRegistrationEvent.OtpChanged(it)) },
                label = { Text("Enter OTP") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Verify Button
            Button(
                onClick = { viewModel.onEvent(StudentRegistrationEvent.VerifyOtp) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Verify OTP")
            }
        }

        // Show loading or error messages
        if (state.isLoading) {
            CircularProgressIndicator()
        }

        state.errorMessage?.let { error ->
            Text(text = error, color = Color.Red)
        }

        // Navigate on success
        LaunchedEffect(state.isRegistrationSuccessful) {
            if (state.isRegistrationSuccessful) {
                onRegistrationSuccess()
            }
        }
    }
}