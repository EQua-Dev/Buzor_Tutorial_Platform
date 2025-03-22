package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.courses.createcourse

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter

@Composable
fun SessionOptionsScreen(
    viewModel: CreateCourseViewModel,
    onCreateCourse: () -> Unit
) {
    val state = viewModel.state.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Course Title and Description Preview
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(state.title, style = MaterialTheme.typography.headlineSmall)
            IconButton(onClick = { /* Show bottom sheet */ }) {
                Icon(imageVector = Icons.Default.Visibility, contentDescription = "Preview Description")
            }
        }

        // Cover Image
        if (state.coverImage.isNotEmpty()) {
            Image(
                painter = rememberImagePainter(state.coverImage),
                contentDescription = "Cover Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        // Private Sessions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Allow Private Sessions?")
            Row {
                Button(onClick = { viewModel.onEvent(CreateCourseEvent.PrivateSessionChanged(true)) }) {
                    Text("Yes")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { viewModel.onEvent(CreateCourseEvent.PrivateSessionChanged(false)) }) {
                    Text("No")
                }
            }
        }

        if (state.allowPrivateSessions) {
            OutlinedTextField(
                value = state.privateSessionPrice,
                onValueChange = { viewModel.onEvent(CreateCourseEvent.PrivateSessionPriceChanged(it)) },
                label = { Text("Private Session Price") },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text("No Private Sessions")
        }

        // Group Sessions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Allow Group Sessions?")
            Row {
                Button(onClick = { viewModel.onEvent(CreateCourseEvent.GroupSessionChanged(true)) }) {
                    Text("Yes")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { viewModel.onEvent(CreateCourseEvent.GroupSessionChanged(false)) }) {
                    Text("No")
                }
            }
        }

        if (state.allowGroupSessions) {
            // Max Seats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Max Seats")
                Row {
                    IconButton(onClick = { viewModel.onEvent(CreateCourseEvent.DecreaseMaxSeats) }) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease Seats")
                    }
                    Text("${state.maxSeats}")
                    IconButton(onClick = { viewModel.onEvent(CreateCourseEvent.IncreaseMaxSeats) }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Increase Seats")
                    }
                }
            }

            // Group Session Price
            OutlinedTextField(
                value = state.groupSessionPrice,
                onValueChange = { viewModel.onEvent(CreateCourseEvent.GroupSessionPriceChanged(it)) },
                label = { Text("Group Session Price") },
                modifier = Modifier.fillMaxWidth()
            )

            // Date and Time Picker
            Button(
                onClick = { /* Open date and time picker */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Select Session Date and Time")
            }
        } else {
            Text("No Group Sessions")
        }

        // Create Course Button
        Button(
            onClick = onCreateCourse,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Course")
        }
    }
}