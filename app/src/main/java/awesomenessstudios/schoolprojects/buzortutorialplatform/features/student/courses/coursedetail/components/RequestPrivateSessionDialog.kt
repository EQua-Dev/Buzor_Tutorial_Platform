package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.courses.coursedetail.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import awesomenessstudios.schoolprojects.buzortutorialplatform.components.DateTimePickerDialog
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.courses.coursedetail.presentation.NewSingleSessionData
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.courses.createcourse.CreateCourseEvent
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.getDate
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestPrivateSessionDialog(
    data: NewSingleSessionData,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onValueChange: (NewSingleSessionData) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Request Private Session") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { showPicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Rounded.CalendarMonth, contentDescription = "Date Time Picker")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Session Date and Time")
                }

                if (selectedDate != null && selectedTime != null) {
                    val formattedDateTime = remember(selectedDate, selectedTime) {
                        "${selectedDate?.format(DateTimeFormatter.ISO_DATE)} at ${selectedTime?.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault()))}"
                    }
                    Text("Selected: $formattedDateTime", style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = data.startTime.isNotBlank(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    if (showPicker) {
        DateTimePickerDialog(
            show = showPicker,
            onDismiss = { showPicker = false },
            onDateTimeSelected = { date, time ->
                selectedDate = date
                selectedTime = time
                val millis = date
                    .atTime(time)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
                onValueChange(data.copy(startTime = millis.toString()))
            }
        )
    }
}