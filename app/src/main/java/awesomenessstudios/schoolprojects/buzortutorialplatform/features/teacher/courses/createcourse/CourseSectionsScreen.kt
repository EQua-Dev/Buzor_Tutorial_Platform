package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.courses.createcourse

import android.graphics.Paint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.NavigateNext
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import awesomenessstudios.schoolprojects.buzortutorialplatform.components.FilePicker
import awesomenessstudios.schoolprojects.buzortutorialplatform.components.ImagePicker
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.CourseSection
import coil.compose.rememberImagePainter


@Composable
fun CourseSectionsScreen(
    viewModel: CreateCourseViewModel,
    onNext: () -> Unit
) {
    val state = viewModel.state.value

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "New Course Content Sections",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))

        ImagePicker { uri ->
            viewModel.onEvent(CreateCourseEvent.CoverImageChanged(uri.toString()))
        }
        if (state.coverImage.isNotEmpty()) {
            Image(
                painter = rememberImagePainter(state.coverImage),
                contentDescription = "Course Cover Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Text("Select a cover image for your course.", color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Add Sections", style = MaterialTheme.typography.titleMedium)
            Button(
                onClick = { viewModel.onEvent(CreateCourseEvent.AddSection) },
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add Section")
                Text("Add Section")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (state.sections.isEmpty()) {
            Text("No sections added yet. Click 'Add Section' to start.", color = MaterialTheme.colorScheme.onSecondaryContainer)
        } else {
            state.sections.forEachIndexed { index, section ->
                SectionItem(index = index, section = section, viewModel = viewModel)
                if (index < state.sections.lastIndex) {
                    Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f), thickness = 1.dp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            enabled = state.sections.isNotEmpty() && state.sections.all { it.title.isNotEmpty() && it.material.isNotEmpty() },
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

@Composable
fun SectionItem(index: Int, section: CourseSection, viewModel: CreateCourseViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = section.title,
            onValueChange = { viewModel.onEvent(CreateCourseEvent.SectionTitleChanged(index, it)) },
            label = { Text("Section ${index + 1} Title") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        FilePicker { uri ->
            viewModel.onEvent(CreateCourseEvent.SectionMaterialChanged(index, uri.toString()))
        }
        Text(
            "Material: ${if (section.material.isNotEmpty()) "Selected" else "Not Selected"}",
            color = if (section.material.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondaryContainer
        )
        if (section.material.isNotEmpty()) {
            Text("Preview: ${section.material.substringAfterLast("/")}", style = MaterialTheme.typography.bodySmall)
        }
        OutlinedTextField(
            value = section.footnote,
            onValueChange = { viewModel.onEvent(CreateCourseEvent.SectionFootnoteChanged(index, it)) },
            label = { Text("Footnote (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            if (index > 0) {
                IconButton(onClick = { viewModel.onEvent(CreateCourseEvent.RemoveSection(index)) }) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Remove Section",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
