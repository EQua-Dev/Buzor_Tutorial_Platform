package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.courses.createcourse

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import awesomenessstudios.schoolprojects.buzortutorialplatform.components.StepIndicator

@Composable
fun CreateCourseFlowScreen(/*navController: NavController,*/ onCourseCreated: () -> Unit) {
    val viewModel: CreateCourseViewModel = hiltViewModel()
    val currentStep by viewModel.currentStep.collectAsState()
    val state = viewModel.state.value

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
            onCourseCreated()
        }
    }
}