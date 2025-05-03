package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.courses.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Course
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.sessions.presentation.AvailableSessionsTab
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.sessions.presentation.MySessionsTab
import awesomenessstudios.schoolprojects.buzortutorialplatform.navigation.Screen
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentCoursesScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: StudentCoursesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Available Courses", "My Courses")
    val studentUid = FirebaseAuth.getInstance().currentUser!!.uid


    LaunchedEffect(Unit) {
        viewModel.onEvent(StudentCoursesEvent.LoadStudentData)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Courses") }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {

                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("Available Courses")
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("My Courses")
                }
            }
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        LoadingIndicator()
                    }
                }

                state.error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        ErrorState(
                            message = state.error!!,
                            onRetry = { viewModel.onEvent(StudentCoursesEvent.LoadStudentData) }
                        )
                    }
                }

                state.courses.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        EmptyState(grade = state.studentGrade)
                    }
                }

                else -> {


                        when (selectedTab) {
                            0 -> {
                                val availableCourses = state.courses.filterNot { course ->
                                    course.enrolledStudents.contains(studentUid)
                                }
                                CoursesList(
                                    courses = availableCourses,
                                    onCourseClick = { courseId ->
                                        navController.navigate(
                                            Screen.StudentCourseDetailScreen.route.replace(
                                                "{courseId}",
                                                courseId
                                            )
                                        )
                                    }
                                )
                            }

                            1 -> {
                                val myCourses = state.courses.filter { course ->
                                    course.enrolledStudents.contains(studentUid)
                                }
                                CoursesList(
                                    courses = myCourses,
                                    onCourseClick = { courseId ->
                                        navController.navigate(
                                            Screen.StudentCourseDetailScreen.route.replace(
                                                "{courseId}",
                                                courseId
                                            )
                                        )
                                    }
                                )
                            }
                        }


                }
            }
        }


    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
private fun EmptyState(grade: String?) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (!grade.isNullOrBlank()) {
                    "No courses available for grade $grade"
                } else {
                    "No courses available"
                },
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun CoursesList(
    courses: List<Course>,
    onCourseClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(courses) { course ->
            CourseItem(
                course = course,
                modifier = Modifier.padding(vertical = 8.dp),
                onClick = { onCourseClick(course.id) }
            )
        }
    }
}

@Composable
private fun CourseItem(
    course: Course,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                model = course.coverImage,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = course.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${course.subject} • ${course.targetGrades.joinToString(", ")}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Price: ₦${course.price}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun CourseCard(course: Course, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
    {
        Column(
            modifier = Modifier.padding(16.dp)
        )
        {
            // Cover Image
            AsyncImage(
                model = course.coverImage,
                contentDescription = "Course Cover",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = course.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Subject and Grade
            Text(
                text = "${course.subject} • ${course.targetGrades.joinToString(", ")}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Price
            Text(
                text = "Price: ₦${course.price}",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            // View Details Button
            /*  Button(
                  onClick = {
                      navController.navigate("course_details/${course.id}")
                  },
                  modifier = Modifier.fillMaxWidth()
              ) {
                  Text("View Course Details")
              }*/
        }
    }
}