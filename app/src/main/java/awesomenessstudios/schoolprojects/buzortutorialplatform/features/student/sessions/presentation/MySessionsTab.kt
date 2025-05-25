package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.sessions.presentation

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Session
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.sessions.presentation.SessionCard
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.getDate
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MySessionsTab(
    mySessions: List<Session>,
    courseTitles: Map<String, String>,
    onOpenSession: (sessionLink: String, showWebView: Boolean) -> Unit
) {

    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    if (mySessions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No upcoming sessions.", style = MaterialTheme.typography.bodyMedium)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(mySessions) { session ->
                SessionCard(
                    enableJoin = !session.students.contains(auth.currentUser!!.uid),
                    status = session.status,
                    date = getDate(session.startTime.trim().toLong(), "EEE, dd MMM yyyy"),
                    time = getDate(session.startTime.trim().toLong(), "hh:mm a"),
                    course = courseTitles[session.courseId] ?: "Unknown Course",
                    price = session.price,
                    typeIcon = if (session.type == "Group") Icons.Default.Group else Icons.Default.Person,
                    typeText = if (session.type == "Group") "Group" else "One-on-One",
                    type = if (session.type == "Group") "Group" else "Single",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(session.sessionLink))
                        context.startActivity(intent)
//                        onOpenSession(session.sessionLink, true)
                    }
                )
            }
        }
    }
}
