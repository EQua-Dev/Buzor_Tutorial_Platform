package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.auth.presentation.signup

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Message
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.PersonOutline
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import awesomenessstudios.schoolprojects.buzortutorialplatform.R
import awesomenessstudios.schoolprojects.buzortutorialplatform.components.AssimOutlinedDropdown
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.auth.presentation.signup.StudentRegistrationEvent
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.auth.presentation.signup.StudentRegistrationViewModel
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.auth.presentation.login.LoadingDialog
import awesomenessstudios.schoolprojects.buzortutorialplatform.ui.theme.BuzorTutorialPlatformTheme
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

    val focusRequesterLastName = remember { FocusRequester() }
    val focusRequesterEmail = remember { FocusRequester() }
    val focusRequesterPassword = remember { FocusRequester() }
    val focusRequesterPhone = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current


    // Error Message Display
    state.value.errorMessage?.let { error ->
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(TeacherRegistrationEvent.DismissError) },
            title = { Text("Registration Error") },
            text = { Text(error) },
            confirmButton = {
                Button(onClick = { viewModel.onEvent(TeacherRegistrationEvent.DismissError) }) {
                    Text("Okay")
                }
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = "Teacher Registration",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            if (state.value.isLoading) {
                LoadingDialog(loadingText = "Registering Teacher...")
            } else if (state.value.isOtpSent) {
                // OTP Verification Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Message,
                            contentDescription = "OTP Sent Icon",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Enter OTP",
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "An OTP has been sent to your device for verification.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        OutlinedTextField(
                            value = state.value.otp,
                            onValueChange = { viewModel.onEvent(TeacherRegistrationEvent.OtpChanged(it)) },
                            label = { Text("OTP") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            leadingIcon = { Icon(Icons.Rounded.VpnKey, contentDescription = "OTP Icon") },
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true
                        )
                        Button(
                            onClick = { viewModel.onEvent(TeacherRegistrationEvent.VerifyOtp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(Icons.Rounded.CheckCircle, contentDescription = "Verify OTP Icon", modifier = Modifier.padding(end = 8.dp))
                            Text("Verify OTP", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            } else
            {
                // Registration Form
                OutlinedTextField(
                    value = state.value.firstName,
                    onValueChange = { viewModel.onEvent(TeacherRegistrationEvent.FirstNameChanged(it)) },
                    label = { Text("First Name") },
                    placeholder = { Text("John") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = "First Name Icon") },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusRequesterLastName.requestFocus() }
                    )
                )
                OutlinedTextField(
                    value = state.value.lastName,
                    onValueChange = { viewModel.onEvent(TeacherRegistrationEvent.LastNameChanged(it)) },
                    label = { Text("Last Name") },
                    placeholder = { Text("Doe") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Rounded.PersonOutline, contentDescription = "Last Name Icon") },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusRequesterEmail.requestFocus() }
                    )
                )
                OutlinedTextField(
                    value = state.value.email,
                    onValueChange = { viewModel.onEvent(TeacherRegistrationEvent.EmailChanged(it)) },
                    label = { Text("Email") },
                    placeholder = { Text("teacher@example.com") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Rounded.Email, contentDescription = "Email Icon") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusRequesterPassword.requestFocus() }
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,

                )
                var passwordVisible by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = state.value.password,
                    onValueChange = { viewModel.onEvent(TeacherRegistrationEvent.PasswordChanged(it)) },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = "Password Icon") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusRequesterPhone.requestFocus() }
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = state.value.phoneNumber,
                    onValueChange = { viewModel.onEvent(TeacherRegistrationEvent.PhoneNumberChanged(it)) },
                    label = { Text("Phone Number") },
                    placeholder = { Text("+234 801 234 5678") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Rounded.Phone, contentDescription = "Phone Number Icon") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
                    /*keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            viewModel.onEvent(TeacherRegistrationEvent.Register(activity!!))
                        }
                    ),*/
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )
                // Custom Dropdown for Subjects (Assuming you have a composable named AssimOutlinedDropdown)
                // Make sure 'subjects' is defined in the scope where this composable is used.
//                val subjects = remember { listOf("Mathematics", "Science", "English", "History", "Art", "Computer Studies") } // Example subjects
                val subjectNames = remember { subjects.map { it.first } }

                AssimOutlinedDropdown(
                    label = stringResource(id = R.string.subjects_label),
                    hint = stringResource(id = R.string.subjects_hint),
                    options = subjectNames,
                    selectedValue = state.value.subjects.joinToString(", "),
                    onValueSelected = { viewModel.onEvent(TeacherRegistrationEvent.SubjectsChanged(it.toString())) },
                    isCompulsory = true,
                    isSearchable = true,
                    leadingIcon = { Icon(Icons.Rounded.Book, contentDescription = "Subjects Icon") },
                    shape = RoundedCornerShape(8.dp),

                )

                Button(
                    onClick = { viewModel.onEvent(TeacherRegistrationEvent.Register(activity!!)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Rounded.PersonAdd, contentDescription = "Register Icon", modifier = Modifier.padding(end = 8.dp))
                    Text("Register", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        // Navigate on success
        LaunchedEffect(state.value.isRegistrationSuccessful) {
            if (state.value.isRegistrationSuccessful) {
                Toast.makeText(context, "Registration Successful!", Toast.LENGTH_SHORT).show()
                onRegistrationSuccess()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssimOutlinedDropdown(
    label: String,
    hint: String,
    options: List<String>,
    selectedValue: String,
    onValueSelected: (String) -> Unit,
    isCompulsory: Boolean = false,
    isSearchable: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    error: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val filteredOptions = remember(options, searchText) {
        if (isSearchable && searchText.isNotBlank()) {
            options.filter { it.contains(searchText, ignoreCase = true) }
        } else {
            options
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = { if (isSearchable) searchText = it },
            label = {
                val labelText = if (isCompulsory) "$label *" else label
                Text(labelText)
            },
            placeholder = { Text(hint) },
            leadingIcon = leadingIcon,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .clickable { expanded = !expanded },
            readOnly = !isSearchable,
            isError = error != null,
            supportingText = { if (error != null) Text(error, color = MaterialTheme.colorScheme.error) },
            shape = shape
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (filteredOptions.isEmpty() && isSearchable && searchText.isNotBlank()) {
                DropdownMenuItem(text = { Text("No matching subjects found") }, onClick = {})
            } else {
                filteredOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(text = option) },
                        onClick = {
                            onValueSelected(option)
                            expanded = false
                            searchText = "" // Clear search text after selection
                        }
                    )
                }
            }
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OTPPreview(modifier: Modifier = Modifier) {
    BuzorTutorialPlatformTheme {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(12.dp), contentAlignment = Alignment.Center){
            Column {
                Text(text = "Enter OTP Sent to your device", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center,)
                // OTP Input Field
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    label = { Text("Enter OTP") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Verify Button
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Verify OTP")
                }
            }

        }
    }

}