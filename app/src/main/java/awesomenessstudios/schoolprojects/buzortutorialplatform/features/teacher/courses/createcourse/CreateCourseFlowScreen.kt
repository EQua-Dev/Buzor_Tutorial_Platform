package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.courses.createcourse

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import awesomenessstudios.schoolprojects.buzortutorialplatform.components.StepIndicator
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.LoadingDialog

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateCourseFlowScreen(/*navController: NavController,*/ onCourseCreated: () -> Unit) {
    val viewModel: CreateCourseViewModel = hiltViewModel()
    val currentStep by viewModel.currentStep.collectAsState()
    val state = viewModel.state.value

    if (state.isLoading){
        LoadingDialog(modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)))
    }

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