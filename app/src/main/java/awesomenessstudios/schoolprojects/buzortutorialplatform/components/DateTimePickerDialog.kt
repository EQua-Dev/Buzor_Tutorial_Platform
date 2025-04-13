package awesomenessstudios.schoolprojects.buzortutorialplatform.components

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar

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
