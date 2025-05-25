package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.sessions.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.GroupSession
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.sessions.presentation.SessionCard
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.getDate
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AvailableSessionsTab(
    availableSessions: List<GroupSession>,
    courseTitles: Map<String, String>,
    onJoin: (amount: Double, courseTitle: String, courseId: String, teacherId: String, sessionId: String) -> Unit
) {

    val auth = FirebaseAuth.getInstance()

    if (availableSessions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No available group sessions.", style = MaterialTheme.typography.bodyMedium)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(availableSessions) { session ->
                SessionCard(
                    enableJoin = !session.students.contains(auth.currentUser!!.uid),
                    date = getDate(session.startTime.trim().toLong(), "EEE, dd MMM yyyy"),
                    time = getDate(session.startTime.trim().toLong(), "hh:mm a"),
                    course = courseTitles[session.courseId] ?: "Unknown Course",
                    price = session.price,
                    typeIcon = if (session.type == "Group") Icons.Default.Group else Icons.Default.Person,
                    typeText = if (session.type == "Group") "Group" else "One-on-One",
                    isStudent = true,
                    type = if (session.type == "Group") "Group" else "One-on-One",
                    onJoin = {
                        onJoin(
                            session.price.toDouble(),
                            courseTitles[session.courseId] ?: "Unknown Course",
                            session.courseId,
                            session.teacherId,
                            session.id
                        )
                    },
                    takenSeats = session.students.size,
                    maxSeats = session.maxAttendance,
                    onClick = {}
                )
            }
        }
    }
}
