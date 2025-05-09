package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.sessions.presentation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.enums.SessionStatus
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.GroupSession
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.SingleSession
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.getDate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun UpcomingSessionsTab(
    groupSessions: List<GroupSession>,
    singleSessions: List<SingleSession>,
    courseTitles: Map<String, String>,
    viewModel: TeacherSessionViewModel = hiltViewModel(),
    onOpenSession: (sessionLink: String, showWebView: Boolean) -> Unit
) {

    val context = LocalContext.current
    val showWebView = remember { mutableStateOf(false) }
    val sessionLink = remember { mutableStateOf("") }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(groupSessions) { session ->
            val courseTitle = courseTitles[session.courseId] ?: "Loading..."
            SessionCard(
                date = getDate(session.startTime.trim().toLong(), "EEE, dd MMM yyyy"),
                time = getDate(session.startTime.trim().toLong(), "hh:mm a"),
                course = courseTitle,
                price = session.price,
                typeIcon = Icons.Default.Groups,
                type = "Group",
                typeText = "${session.students.size} of ${session.maxAttendance}",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(session.sessionLink))
                    context.startActivity(intent)
//                    onOpenSession(session.sessionLink, true)
//                    sessionLink.value = session.sessionLink // 🔥 Save the link
//                    showWebView.value = true // 🔥 Trigger WebView
                }
            )
        }

        items(singleSessions) { session ->
            val courseTitle = courseTitles[session.courseId] ?: "Loading..."
            LaunchedEffect(session) {
                viewModel.loadStudentData(session.studentId)
            }
            SessionCard(
                status = null,
                date = getDate(session.startTime.trim().toLong(), "EEE dd MMM yyyy"),
                time = getDate(session.startTime.trim().toLong(), "hh:mm a"),
                course = courseTitle,
                price = session.price,
                typeIcon = Icons.Default.Person,
                type = "Single",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(session.sessionLink))
                    context.startActivity(intent)
//                    onOpenSession(session.sessionLink, true)

                },
                typeText = "${viewModel.state.student?.firstName} ${viewModel.state.student?.lastName}"
            )
        }
    }
}

@Composable
fun SessionCard(
    status: String? = null,
    date: String,
    time: String,
    course: String,
    price: String,
    typeIcon: ImageVector,
    typeText: String,
    type: String,
    isStudent: Boolean = false,
    onJoin: (() -> Unit?)? = null,
    takenSeats: Int = 0,
    maxSeats: Int = 0,
    onClick: () -> Unit,
    viewModel: TeacherSessionViewModel = hiltViewModel()
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(0.7f)) {
                    Text(text = "Date: $date")
                    Text(text = "Time: $time")
                    Text(text = "Course: $course")
                    Text(text = "Price: ₦$price")
                }
                Divider(
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(4.dp)
                        .padding(horizontal = 4.dp)
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(0.3f)
                ) {
                    if (status != null)
                        Text(
                            text = status.lowercase(),
                            color = when (status) {
                                SessionStatus.PENDING.name -> Color(0xFFFFA500) // Orange
                                SessionStatus.ACCEPTED.name -> Color.Green
                                else -> Color.Red
                            }
                        )

                    Icon(imageVector = typeIcon, contentDescription = null)
                    Text(text = typeText)
                    if (type == "Group" && isStudent) {
                        Text(text = "${maxSeats.minus(takenSeats)} left")
                        Button(onClick = {
                            if (onJoin != null) {
                                onJoin()
                            }
                        }) {
                            Text(text = "Join")
                        }
                    }
                }
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