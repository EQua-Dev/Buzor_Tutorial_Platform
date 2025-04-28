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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

@RequiresApi(Build.VERSION_CODES.O)
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


    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                Text("Request Private Session", style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(16.dp))

                // Date and Time Picker
                Button(
                    onClick = { showPicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Select Session Date and Time")
                }
           /* OutlinedTextField(
                    value = getDate(data.startTime.toLong() ?: System.currentTimeMillis().toLong(), "EEE, dd MMM yyyy | hh:mm a"),
                    onValueChange = { onValueChange(data.copy(startTime = it)) },
                    label = { Text("Start Time") },
                    modifier = Modifier.fillMaxWidth().clickable { showPicker = true }
                )*/
                DateTimePickerDialog(
                    show = showPicker,
                    onDismiss = { showPicker = false },
                    onDateTimeSelected = { date, time ->
                        selectedDate = date
                        selectedTime = time
                    }
                )

                selectedDate?.let { date ->
                    selectedTime?.let { time ->
                        val millis = date
                            .atTime(time)
                            .atZone(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli()

                        onValueChange(data.copy(startTime = millis.toString()))
                        Text(
                            "Selected: ${date.format(DateTimeFormatter.ISO_DATE)} at ${
                                time.format(
                                    DateTimeFormatter.ofPattern("hh:mm a")
                                )
                            }"
                        )
                    }
                }


                /*     Spacer(modifier = Modifier.height(8.dp))

                     OutlinedTextField(
                         value = data.type,
                         onValueChange = { onValueChange(data.copy(type = it)) },
                         label = { Text("Session Type") },
                         modifier = Modifier.fillMaxWidth()
                     )

                     Spacer(modifier = Modifier.height(8.dp))

                     OutlinedTextField(
                         value = data.price,
                         onValueChange = { onValueChange(data.copy(price = it)) },
                         label = { Text("Price (€)") },
                         modifier = Modifier.fillMaxWidth()
                     )
     */
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = { onDismiss() }) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onConfirm() }) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}
