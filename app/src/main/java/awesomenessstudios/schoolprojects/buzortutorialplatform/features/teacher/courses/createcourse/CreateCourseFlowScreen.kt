package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.courses.createcourse

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import awesomenessstudios.schoolprojects.buzortutorialplatform.components.StepIndicator
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.LoadingDialog


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateCourseFlowScreen(/*navController: NavController,*/ onCourseCreated: () -> Unit) {
    val viewModel: CreateCourseViewModel = hiltViewModel()
    val currentStep by viewModel.currentStep.collectAsState()
    val state = viewModel.state.value
    val context = LocalContext.current

    if (state.isLoading) {
        LoadingDialog(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
            loadingText = "Creating Course..."
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            StepIndicator(currentStep = currentStep, totalSteps = 3)

            when (currentStep) {
                1 -> CourseDetailsScreen(viewModel, onNext = { viewModel.onEvent(CreateCourseEvent.NextStep) })
                2 -> CourseSectionsScreen(viewModel, onNext = { viewModel.onEvent(CreateCourseEvent.NextStep) })
                3 -> SessionOptionsScreen(viewModel, onCreateCourse = { viewModel.onEvent(CreateCourseEvent.CreateCourse) })
            }
        }

        // Navigate on Success
        LaunchedEffect(state.isCourseCreated) {
            if (state.isCourseCreated) {
                Toast.makeText(context, "Course created successfully!", Toast.LENGTH_SHORT).show()
                onCourseCreated()
            }
        }

        state.errorMessage?.let { error ->
            AlertDialog(
                onDismissRequest = { viewModel.state.value.copy(errorMessage =  null) },
                title = { Text("Error Creating Course") },
                text = { Text(error) },
                confirmButton = {
                    Button(onClick = { viewModel.state.value.copy(errorMessage =  null) }) {
                        Text("Okay")
                    }
                }
            )
        }
    }
}

@Composable
fun StepIndicator(currentStep: Int, totalSteps: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..totalSteps) {
            val isCurrent = i == currentStep
            val color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            val fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal

            Text(
                text = "$i",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = fontWeight, color = color),
                modifier = Modifier
                    .size(32.dp)
                    .border(1.dp, color, CircleShape)
                    .wrapContentSize(Alignment.Center)
            )
            if (i < totalSteps) {
                Divider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                    thickness = 1.dp
                )
            }
        }
    }
}
