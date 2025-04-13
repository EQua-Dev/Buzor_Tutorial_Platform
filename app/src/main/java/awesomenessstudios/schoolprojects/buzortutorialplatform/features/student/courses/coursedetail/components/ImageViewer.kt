package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.courses.coursedetail.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
fun ImageViewer(url: String) {
    AsyncImage(
        model = url,
        contentDescription = "Course content image",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Fit
    )
}