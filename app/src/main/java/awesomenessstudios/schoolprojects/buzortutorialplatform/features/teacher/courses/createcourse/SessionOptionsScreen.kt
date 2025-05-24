package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.courses.createcourse

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import awesomenessstudios.schoolprojects.buzortutorialplatform.components.DateTimePickerDialog
import coil.compose.rememberImagePainter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SessionOptionsScreen(
    viewModel: CreateCourseViewModel,
    onCreateCourse: () -> Unit
) {
    val state = viewModel.state.value
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }
    val formattedDate = remember(selectedDate) { selectedDate?.format(DateTimeFormatter.ISO_DATE) ?: "Not Selected" }
    val formattedTime = remember(selectedTime) { selectedTime?.format(DateTimeFormatter.ofPattern("hh:mm a")) ?: "Not Selected" }

    var showDateTimePicker by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Session Options",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(state.title, style = MaterialTheme.typography.titleMedium)
        if (state.coverImage.isNotEmpty()) {
            Image(
                painter = rememberImagePainter(state.coverImage),
                contentDescription = "Course Cover Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }

        // Private Sessions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Allow Private Sessions?", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = state.allowPrivateSessions,
                onCheckedChange = { viewModel.onEvent(CreateCourseEvent.PrivateSessionChanged(it)) }
            )
        }
        if (state.allowPrivateSessions) {
            OutlinedTextField(
                value = state.privateSessionPrice,
                onValueChange = { viewModel.onEvent(CreateCourseEvent.PrivateSessionPriceChanged(it)) },
                label = { Text("Private Session Price (₦)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Rounded.Payments, contentDescription = "Private Session Price Icon") },
                shape = RoundedCornerShape(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Group Sessions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Allow Group Sessions?", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = state.allowGroupSessions,
                onCheckedChange = { viewModel.onEvent(CreateCourseEvent.GroupSessionChanged(it)) }
            )
        }
        if (state.allowGroupSessions) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Max Seats", style = MaterialTheme.typography.bodyLarge)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.onEvent(CreateCourseEvent.DecreaseMaxSeats) }) {
                        Icon(Icons.Rounded.Remove, contentDescription = "Decrease Seats")
                    }
                    Text("${state.maxSeats}", style = MaterialTheme.typography.bodyLarge)
                    IconButton(onClick = { viewModel.onEvent(CreateCourseEvent.IncreaseMaxSeats) }) {
                        Icon(Icons.Rounded.Add, contentDescription = "Increase Seats")
                    }
                }
            }

            OutlinedTextField(
                value = state.groupSessionPrice,
                onValueChange = { viewModel.onEvent(CreateCourseEvent.GroupSessionPriceChanged(it)) },
                label = { Text("Group Session Price (₦)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Rounded.Payments, contentDescription = "Group Session Price Icon") },
                shape = RoundedCornerShape(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("First Session Date:", style = MaterialTheme.typography.bodyLarge)
                Button(onClick = { showDateTimePicker = true }, shape = RoundedCornerShape(8.dp)) {
                    Icon(Icons.Rounded.CalendarMonth, contentDescription = "Date Time Picker Icon", modifier = Modifier.padding(end = 8.dp))
                    Text("Select")
                }
            }
/*
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Session Time:", style = MaterialTheme.typography.bodyLarge)
                Button(onClick = { showTimePicker = true }, shape = RoundedCornerShape(8.dp)) {
                    Text(formattedTime)
                }
            }*/

            if (selectedDate != null && selectedTime != null) {
                val selectedDateTimeNigeria = LocalDateTime.of(selectedDate, selectedTime)
                viewModel.onEvent(CreateCourseEvent.GroupSessionDateChanged(selectedDateTimeNigeria.toEpochMilliNigeria().toString()))
                Text(
                    "Selected Session Time: $formattedDate at $formattedTime (WAT)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        } else {
            Text("Group sessions are disabled for this course.", color = MaterialTheme.colorScheme.onSecondaryContainer)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onCreateCourse,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            enabled = state.title.isNotEmpty() && state.description.isNotEmpty() && state.price.isNotEmpty() && state.sections.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(Icons.Rounded.Check, contentDescription = "Create Course Icon", modifier = Modifier.padding(end = 8.dp))
            Text("Create Course", style = MaterialTheme.typography.bodyLarge)
        }
    }
    // DateTimePickerDialog usage
    if (showDateTimePicker) {
        DateTimePickerDialog(
            show = showDateTimePicker,
            onDismiss = { showDateTimePicker = false },
            onDateTimeSelected = { date, time ->
                selectedDate = date
                selectedTime = time
            }
        )
    }
}


// Extension function to get milliseconds in Nigeria time zone (as requested previously)
@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.toEpochMilliNigeria(): Long {
    val nigeriaZone = ZoneId.of("Africa/Lagos")
    return this.atZone(nigeriaZone).toInstant().toEpochMilli()
}