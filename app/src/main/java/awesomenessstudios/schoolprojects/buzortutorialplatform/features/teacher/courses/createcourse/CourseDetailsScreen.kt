package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.courses.createcourse

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Grade
import androidx.compose.material.icons.rounded.NavigateNext
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import awesomenessstudios.schoolprojects.buzortutorialplatform.R
import awesomenessstudios.schoolprojects.buzortutorialplatform.components.AssimOutlinedDropdown
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Constants.subjects


@Composable
fun CourseDetailsScreen(
    viewModel: CreateCourseViewModel,
    onNext: () -> Unit
) {
    val state = viewModel.state.value
    val context = LocalContext.current
    val subjects = remember { subjects }

//    val subjects = remember { listOf("Mathematics" to "Mathematics", "Science" to "Science", "History" to "History", "English" to "English", "Computer Studies" to "Computer Studies") }
    val grades = remember {    listOf(
        "JSS 1" to "JSS 1",
        "JSS 2" to "JSS 2",
        "JSS 3" to "JSS 3",
        "SSS 1" to "SSS 1",
        "SSS 2" to "SSS 2",
        "SSS 3" to "SSS 3"
    ) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Enter New Course Details",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))

        AssimOutlinedDropdown(
            label = stringResource(id = R.string.course_subject_label),
            hint = stringResource(id = R.string.course_subject_hint),
            options = subjects,
            selectedValue = state.subject,
            onValueSelected = { viewModel.onEvent(CreateCourseEvent.SubjectChanged(it.toString())) },
            isCompulsory = true,
            isSearchable = false,
//            leadingIcon = { Icon(Icons.Rounded.Book, contentDescription = "Subject Icon") },
            modifier = Modifier.clip(shape = RoundedCornerShape(8.dp)),

        )

        AssimOutlinedDropdown(
            label = stringResource(id = R.string.course_grades_label),
            hint = stringResource(id = R.string.course_grades_hint),
            options = grades,
            selectedValue = state.targetGrades,
            onValueSelected = { viewModel.onEvent(CreateCourseEvent.TargetGradesChanged(it.toString())) },
            isCompulsory = true,
            isSearchable = false,
//            leadingIcon = { Icon(Icons.Rounded.Grade, contentDescription = "Grades Icon") },
            modifier = Modifier.clip(shape = RoundedCornerShape(8.dp)),
        )

        OutlinedTextField(
            value = state.title,
            onValueChange = { viewModel.onEvent(CreateCourseEvent.TitleChanged(it)) },
            label = { Text("Course Title") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Rounded.Title, contentDescription = "Title Icon") },
            shape = RoundedCornerShape(8.dp)
        )

        OutlinedTextField(
            value = state.description,
            onValueChange = { viewModel.onEvent(CreateCourseEvent.DescriptionChanged(it)) },
            label = { Text("Course Description") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Rounded.Description, contentDescription = "Description Icon") },
            trailingIcon = {
                IconButton(onClick = {
                    Toast.makeText(context, "AI Description Generation Coming Soon!", Toast.LENGTH_SHORT).show()
                    // viewModel.generateDescription()
                }) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "Generate Description")
                }
            },
            shape = RoundedCornerShape(8.dp),
            minLines = 3
        )

        OutlinedTextField(
            value = state.price,
            onValueChange = { viewModel.onEvent(CreateCourseEvent.PriceChanged(it)) },
            label = { Text("Course Price (₦)") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Rounded.Payments, contentDescription = "Price Icon") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            enabled = state.subject.isNotEmpty() && state.targetGrades.isNotEmpty() && state.title.isNotEmpty() && state.description.isNotEmpty() && state.price.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(Icons.Rounded.NavigateNext, contentDescription = "Next Icon", modifier = Modifier.padding(end = 8.dp))
            Text("Next", style = MaterialTheme.typography.bodyLarge)
        }
    }
}