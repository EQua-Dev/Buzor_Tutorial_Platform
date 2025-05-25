package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.sessions.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.Result
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.SingleSession
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.getDate
import dagger.hilt.android.internal.managers.FragmentComponentManager.findActivity
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun SessionRequestsTab(
    sessionRequests: List<SingleSession>,
    courseTitles: Map<String, String>,
    viewModel: TeacherSessionViewModel = hiltViewModel()
) {


    val context = LocalContext.current
    val activity = remember(context) {
        findActivity(context)
            ?.takeIf { it is FragmentActivity } as? FragmentActivity
    }

    val scope = rememberCoroutineScope()


    LazyColumn(modifier = Modifier.fillMaxSize()) {

        if (sessionRequests.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "You have no session requests")
                }
            }

        }
        items(sessionRequests) { session ->
            val courseTitle = courseTitles[session.courseId] ?: "Loading..."

            SessionRequestCard(
                date = getDate(session.startTime.trim().toLong(), "EEE dd MMM yyyy"),
                time = getDate(session.startTime.trim().toLong(), "hh:mm a"),
                course = courseTitle,
                childName = session.studentId,
                price = session.price,
                onAccept = {
                    scope.launch {
                        activity?.let { fragmentActivity ->
                            viewModel.acceptSession(session.id, fragmentActivity)
                        } ?: run {
                            // Handle case where activity isn't available
                            // Maybe show error or use alternative authentication
                        }

                    }
                },
                onDecline = {
                    scope.launch {
                        activity?.let { fragmentActivity ->
                            viewModel.declineSession(session.id, fragmentActivity)
                        } ?: run {
                            // Handle case where activity isn't available
                            // Maybe show error or use alternative authentication
                        }

                    }
                },
            )
        }
    }
}

@Composable
fun SessionRequestCard(
    date: String,
    time: String,
    course: String,
    childName: String,
    price: String,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    viewModel: TeacherSessionViewModel = hiltViewModel()
) {
    LaunchedEffect(childName) {
        viewModel.loadStudentData(childName)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(0.7f)
            ) {
                Text(text = "Date: $date", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Time: $time", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Course: $course", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "Child Name: ${viewModel.state.student?.firstName} ${viewModel.state.student?.lastName}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(text = "Price: ₦$price", style = MaterialTheme.typography.bodyMedium)
            }

            Divider(
                color = Color.DarkGray,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .padding(horizontal = 8.dp)
            )

            Column(
                modifier = Modifier
                    .weight(0.3f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text("Accept", color = Color.White)
                }

                Button(
                    onClick = onDecline,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Decline", color = Color.White)
                }
            }
        }
    }
}
