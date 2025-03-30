package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.courses.createcourse

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import awesomenessstudios.schoolprojects.buzortutorialplatform.components.FilePicker
import awesomenessstudios.schoolprojects.buzortutorialplatform.components.ImagePicker
import coil.compose.rememberImagePainter

@Composable
fun CourseSectionsScreen(
    viewModel: CreateCourseViewModel,
    onNext: () -> Unit
) {
    val state = viewModel.state.value

    Column(
        Modifier.fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Cover Image Selection
        ImagePicker { uri ->
            viewModel.onEvent(CreateCourseEvent.CoverImageChanged(uri.toString()))
        }
        // Section Title and Add Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Course Sections", style = MaterialTheme.typography.headlineSmall)
            IconButton(onClick = { viewModel.onEvent(CreateCourseEvent.AddSection) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Section")
            }
        }

        // Sections List
        state.sections.forEachIndexed { index, section ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = section.title,
                    onValueChange = {
                        viewModel.onEvent(
                            CreateCourseEvent.SectionTitleChanged(
                                index,
                                it
                            )
                        )
                    },
                    label = { Text("Section ${index + 1} Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                FilePicker { uri ->
                    viewModel.onEvent(CreateCourseEvent.SectionMaterialChanged(index, uri.toString()))
                }

                if (section.material.isNotEmpty()) {
                    Text("Preview: ${section.material}")
                }

                OutlinedTextField(
                    value = section.footnote,
                    onValueChange = {
                        viewModel.onEvent(
                            CreateCourseEvent.SectionFootnoteChanged(
                                index,
                                it
                            )
                        )
                    },
                    label = { Text("Footnote (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (index > 0) {
                    IconButton(onClick = { viewModel.onEvent(CreateCourseEvent.RemoveSection(index)) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove Section"
                        )
                    }
                }
            }
        }

        // Next Button
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Next")
        }
    }
}