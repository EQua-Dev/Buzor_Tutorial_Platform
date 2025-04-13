package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.sessions.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.GroupSession
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.SingleSession
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.HelpMe
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.getDate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun UpcomingSessionsTab(
    groupSessions: List<GroupSession>,
    singleSessions: List<SingleSession>
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(groupSessions) { session ->
            SessionCard(
                isGroup = true,
                date = formatDate(session.startTime),
                time = formatTime(session.startTime),
                course = session.courseId,
                price = session.price,
                typeIcon = Icons.Default.Groups,
                typeText = "${session.students.size} of ${session.maxAttendance}"
            )
        }

        items(singleSessions) { session ->
            SessionCard(
                isGroup = false,
                date = getDate(session.startTime.toLong(), "EEE dd MMM yyyy"),
                time = getDate(session.startTime.toLong(), "hh:mm a"),
                course = session.courseId,
                price = session.price,
                typeIcon = Icons.Default.Person,
                typeText = session.studentId
            )
        }
    }
}

@Composable
fun SessionCard(
    isGroup: Boolean,
    date: String,
    time: String,
    course: String,
    price: String,
    typeIcon: ImageVector,
    typeText: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, Color.LightGray)
            .padding(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(0.7f)) {
                Text(text = "Date: $date")
                Text(text = "Time: $time")
                Text(text = "Course: $course")
                Text(text = "Price: $price")
            }
            Divider(
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .padding(horizontal = 4.dp)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(0.3f)
            ) {
                Icon(imageVector = typeIcon, contentDescription = null)
                Text(text = typeText)
            }
        }
    }
}

fun formatDate(timeMillis: String): String {
    val date = Date(timeMillis.toLongOrNull() ?: 0)
    return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)
}

fun formatTime(timeMillis: String): String {
    val date = Date(timeMillis.toLongOrNull() ?: 0)
    return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date)
}