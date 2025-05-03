package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.courses.coursedetail.presentation

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Course
import awesomenessstudios.schoolprojects.buzortutorialplatform.navigation.Screen


@Composable
fun CourseContentView(
    course: Course,
    currentUserId: String?,
    navController: NavController,
    onTriggerEnroll: () -> Unit,
    onRateTriggered: (star: Int) -> Unit
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
        if (isEnrolled) {
            Spacer(modifier = Modifier.height(24.dp))

            Text("Your Rating:", style = MaterialTheme.typography.titleMedium)

            val existingRating = course.raters[currentUserId]

            if (existingRating != null) {
                Text("You rated this course: $existingRating ⭐", style = MaterialTheme.typography.bodyMedium)
            } else {
                var selectedRating by remember { mutableStateOf(0) }

                Row {
                    (1..5).forEach { star ->
                        IconToggleButton(
                            checked = selectedRating >= star,
                            onCheckedChange = {
                                selectedRating = star
                                onRateTriggered(star)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rate $star",
                                tint = if (selectedRating >= star) Color.Yellow else Color.Gray
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Average Rating:",
                style = MaterialTheme.typography.titleMedium
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = String.format("%.1f", course.rating),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFFFFC107) // amber star yellow
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Average Rating",
                    tint = Color(0xFFFFC107)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "(${course.raters.size} ratings)",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }


        sections.forEach { (title, footnote, link) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                if (isEnrolled) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(title, style = MaterialTheme.typography.titleMedium)
                            Text(footnote, style = MaterialTheme.typography.bodySmall)
                        }

                        Button(
                            onClick = {
                                navController.navigate(
                                    Screen.CourseContentViewerScreen.route.replace(
                                        "{url}",
                                        Uri.encode(link)
                                    )
                                )
                            }
                        ) {
                            Text("View")
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(title, style = MaterialTheme.typography.titleMedium)
                            Text(footnote, style = MaterialTheme.typography.bodySmall)
                        }

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