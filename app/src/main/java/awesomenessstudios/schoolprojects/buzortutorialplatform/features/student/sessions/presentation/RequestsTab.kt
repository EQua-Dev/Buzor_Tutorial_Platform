/*
package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.sessions.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Escrow
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.SingleSession
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.sessions.presentation.TeacherSessionViewModel
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.getDate

@Composable
fun RequestsTab(sessionRequests: List<Escrow>, courseTitles: Map<String, String>) {
    if (sessionRequests.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No pending session requests.", style = MaterialTheme.typography.bodyMedium)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sessionRequests) { request ->
                StudentSessionRequestCard(
                    date = getDate(request.)
                )
                SessionCard(
                    title = courseTitles[request.courseId] ?: "Unknown Course",
                    dateTime = formatDateTime(request.startTime),
                    type = "Requested Session"
                )
            }
        }
    }
}


@Composable
fun StudentSessionRequestCard(
    date: String,
    time: String,
    course: String,
    price: String,
    status: String,
    viewModel: TeacherSessionViewModel = hiltViewModel()
) {

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

                Text(text = "Price: €$price", style = MaterialTheme.typography.bodyMedium)
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
                Text(text = status, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
*/
