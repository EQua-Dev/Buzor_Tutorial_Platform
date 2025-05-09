package awesomenessstudios.schoolprojects.buzortutorialplatform.components

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar

/*
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateTimePickerDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onDateTimeSelected: (LocalDate, LocalTime) -> Unit
) {
    if (!show) return

    val context = LocalContext.current
    val today = remember { Calendar.getInstance() }

    var showTimePicker by remember { mutableStateOf(false) }

    val datePicker = DatePickerDialog(
        context,
        { _, year, month, day ->
            val selectedDate = LocalDate.of(year, month + 1, day)
            showTimePicker = true

            android.app.TimePickerDialog(
                context,
                { _, hour, minute ->
                    val selectedTime = LocalTime.of(hour, minute)
                    onDateTimeSelected(selectedDate, selectedTime)
                    onDismiss()
                },
                today.get(Calendar.HOUR_OF_DAY),
                today.get(Calendar.MINUTE),
                true
            ).show()
        },
        today.get(Calendar.YEAR),
        today.get(Calendar.MONTH),
        today.get(Calendar.DAY_OF_MONTH)
    )

    LaunchedEffect(show) {
        if (show) datePicker.show()
    }
}
*/


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onDateTimeSelected: (LocalDate, LocalTime) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var internalSelectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var internalSelectedTime by remember { mutableStateOf<LocalTime?>(null) }
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Button(
                    onClick = {
                        internalSelectedDate?.let { date ->
                            internalSelectedTime?.let { time ->
                                onDateTimeSelected(date, time)
                                onDismiss()
                            }
                        }
                    },
                    enabled = internalSelectedDate != null && internalSelectedTime != null
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Cancel")
                }
            },
            title = { Text("Select Date and Time") },
            text = {
                Column {
                    if (showDatePicker) {
                        DatePicker(state = datePickerState, showModeToggle = false)
                    }
                    if (showTimePicker) {
                        TimePicker(state = timePickerState)
                    }

                    if (!showDatePicker && !showTimePicker) {
                        Button(onClick = { showDatePicker = true }) {
                            Text("Select Date")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (internalSelectedDate != null) {
                            Button(onClick = { showTimePicker = true }) {
                                Text("Select Time")
                            }
                        }
                    }
                }
            }
        )

        LaunchedEffect(datePickerState.selectedDateMillis) {
            datePickerState.selectedDateMillis?.let {
                internalSelectedDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                showDatePicker = false
                showTimePicker = true // Immediately show time picker after date selection
            }
        }

        LaunchedEffect(timePickerState.hour, timePickerState.minute) {
            if (showTimePicker) {
                internalSelectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
            }
        }
    }
}