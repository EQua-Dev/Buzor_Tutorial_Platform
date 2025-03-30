package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.courses.createcourse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import awesomenessstudios.schoolprojects.buzortutorialplatform.R
import awesomenessstudios.schoolprojects.buzortutorialplatform.components.AssimOutlinedDropdown
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Constants.grades
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Constants.subjects

@Composable
fun CourseDetailsScreen(
    viewModel: CreateCourseViewModel,
    onNext: () -> Unit
) {
    val state = viewModel.state.value

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Subject Dropdown
        var expandedSubject by remember { mutableStateOf(false) }
//        val subjects = listOf("Math", "Science", "History", "English")

        AssimOutlinedDropdown(
            label = stringResource(id = R.string.course_subject_label),
            hint = stringResource(id = R.string.course_subject_hint),
            options = subjects,
            selectedValue = state.subject,
            onValueSelected = { viewModel.onEvent(CreateCourseEvent.SubjectChanged(it.toString())) },
            isCompulsory = true,
//            error = state.genderError?.let { stringResource(id = it) },
            isSearchable = false // Enable search functionality
        )
/*

        ExposedDropdownMenuBox(
            expanded = expandedSubject,
            onExpandedChange = { expandedSubject = !expandedSubject }
        ) {
            OutlinedTextField(
                value = state.subject,
                onValueChange = {},
                label = { Text("Subject") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSubject)
                }
            )

            ExposedDropdownMenu(
                expanded = expandedSubject,
                onDismissRequest = { expandedSubject = false }
            ) {
                subjects.forEach { subject ->
                    DropdownMenuItem(
                        onClick = {
                            viewModel.onEvent(CreateCourseEvent.SubjectChanged(subject))
                            expandedSubject = false
                        }
                    ) {
                        Text(text = subject)
                    }
                }
            }
        }
*/

        // Target Grades Dropdown (Multiple Selection)
        var expandedGrades by remember { mutableStateOf(false) }

        AssimOutlinedDropdown(
            label = stringResource(id = R.string.course_grades_label),
            hint = stringResource(id = R.string.course_grades_hint),
            options = grades,
            selectedValue = state.targetGrades,
            onValueSelected = { viewModel.onEvent(CreateCourseEvent.TargetGradesChanged(it.toString())) },
            isCompulsory = true,
//            error = state.genderError?.let { stringResource(id = it) },
            isSearchable = false // Enable search functionality
        )

        /*ExposedDropdownMenuBox(
            expanded = expandedGrades,
            onExpandedChange = { expandedGrades = !expandedGrades }
        ) {
            OutlinedTextField(
                value = state.targetGrades.joinToString(", "),
                onValueChange = {},
                label = { Text("Target Grades") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGrades)
                }
            )

            ExposedDropdownMenu(
                expanded = expandedGrades,
                onDismissRequest = { expandedGrades = false }
            ) {
                grades.forEach { grade ->
                    DropdownMenuItem(
                        onClick = {
                            viewModel.onEvent(CreateCourseEvent.TargetGradesChanged(grade))
                        }
                    ) {
                        Text(text = grade)
                    }
                }
            }
        }
*/
        // Course Title
        OutlinedTextField(
            value = state.title,
            onValueChange = { viewModel.onEvent(CreateCourseEvent.TitleChanged(it)) },
            label = { Text("Course Title") },
            modifier = Modifier.fillMaxWidth()
        )

        // Course Description with AI Icon
        OutlinedTextField(
            value = state.description,
            onValueChange = { viewModel.onEvent(CreateCourseEvent.DescriptionChanged(it)) },
            label = { Text("Course Description") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { viewModel.generateDescription() }) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Generate Description")
                }
            }
        )

        // Course Price
        OutlinedTextField(
            value = state.price,
            onValueChange = { viewModel.onEvent(CreateCourseEvent.PriceChanged(it)) },
            label = { Text("Course Price") },
            modifier = Modifier.fillMaxWidth()
        )

        // Next Button
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Next")
        }
    }
}