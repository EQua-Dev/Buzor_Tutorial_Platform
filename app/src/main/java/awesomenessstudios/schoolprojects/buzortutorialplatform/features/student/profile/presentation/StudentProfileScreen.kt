package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.LoadingDialog
import coil.compose.AsyncImage

@Composable
fun StudentProfileScreen(
    viewModel: StudentProfileViewModel = hiltViewModel()
) {
    val state = viewModel.state

    LaunchedEffect(key1 = Unit) {
        viewModel.onEvent(StudentProfileEvent.LoadProfile)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        if (state.isLoading) {
            LoadingDialog(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)))
        } else {
            AsyncImage(
                model = state.student.profileImage,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally)
                    .clickable(enabled = state.isEditMode) {
                        // Launch image picker here (not implemented in this block)
                    }
            )

            Spacer(modifier = Modifier.height(16.dp))

            EditableTextField(
                label = "First Name",
                value = state.student.firstName,
                enabled = state.isEditMode,
                onValueChange = { viewModel.onEvent(StudentProfileEvent.FirstNameChanged(it)) }
            )

            EditableTextField(
                label = "Last Name",
                value = state.student.lastName,
                enabled = state.isEditMode,
                onValueChange = { viewModel.onEvent(StudentProfileEvent.LastNameChanged(it)) }
            )

            EditableTextField(
                label = "Phone Number",
                value = state.student.phoneNumber,
                enabled = state.isEditMode,
                onValueChange = { viewModel.onEvent(StudentProfileEvent.PhoneNumberChanged(it)) }
            )

            EditableTextField(
                label = "Grade",
                value = state.student.grade,
                enabled = state.isEditMode,
                onValueChange = { viewModel.onEvent(StudentProfileEvent.GradeChanged(it)) }
            )

            EditableTextField(
                label = "Preferred Subjects",
                value = state.student.preferredSubjects.joinToString(", "),
                enabled = state.isEditMode,
                onValueChange = {
                    viewModel.onEvent(StudentProfileEvent.PreferredSubjectsChanged(it.split(",").map { subject -> subject.trim() }))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { viewModel.onEvent(StudentProfileEvent.ToggleEditMode) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (state.isEditMode) "Cancel" else "Edit")
                }

                Spacer(modifier = Modifier.width(8.dp))

                if (state.isEditMode) {
                    Button(
                        onClick = { viewModel.onEvent(StudentProfileEvent.SaveProfile) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save")
                    }
                }
            }

            state.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = error, color = Color.Red)
            }

            if (state.isUpdateSuccessful) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Profile updated successfully!", color = Color.Green)
            }
        }
    }
}

@Composable
fun EditableTextField(
    label: String,
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}
