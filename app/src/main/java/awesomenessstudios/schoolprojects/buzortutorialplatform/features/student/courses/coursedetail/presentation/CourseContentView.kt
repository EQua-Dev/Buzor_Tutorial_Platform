package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.courses.coursedetail.presentation

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Course


@Composable
fun CourseContentView(
    course: Course,
    currentUserId: String?,
    navController: NavController,
    onTriggerEnroll: () -> Unit
) {
    val isEnrolled = currentUserId != null && course.enrolledStudents.contains(currentUserId)

    Column(Modifier.padding(16.dp)) {
        Text(
            "Enrolled Students: ${course.enrolledStudents.size}",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        val sections = listOf(
            Triple(course.courseNoteOneTitle, course.courseNoteOneFootnote, course.courseNoteOne),
            Triple(course.courseNoteTwoTitle, course.courseNoteTwoFootnote, course.courseNoteTwo),
            Triple(
                course.courseNoteThreeTitle,
                course.courseNoteThreeFootnote,
                course.courseNoteThree
            ),
        ).filter { it.first.isNotBlank() && it.third.isNotBlank() }

        sections.forEach { (title, footnote, link) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(title, style = MaterialTheme.typography.titleMedium)
                        Text(footnote, style = MaterialTheme.typography.bodySmall)
                    }

                    if (isEnrolled) {
                        Button(
                            onClick = {
                                navController.navigate("viewContent?url=${Uri.encode(link)}")
                            },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(16.dp)
                        ) {
                            Text("View")
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.8f),
                                            Color.White
                                        ),
                                        startY = 0f,
                                        endY = 200f
                                    )
                                )
                                .align(Alignment.Center)
                        ) {
                            Button(
                                onClick = onTriggerEnroll,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(16.dp)
                            ) {
                                Text("Enroll to View")
                            }
                        }
                    }
                }
            }
        }
    }
}