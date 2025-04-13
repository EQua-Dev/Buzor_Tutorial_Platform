package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.courses.coursedetail.presentation

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.courses.coursedetail.components.DefaultWebView
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.courses.coursedetail.components.ImageViewer
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.courses.coursedetail.components.PDFViewer
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.courses.coursedetail.components.VideoPlayer
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.FirebaseUrlHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewContentScreen(url: String, navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    // Extract file extension from URL
   /* val fileExtension by remember(url) {
        derivedStateOf {
            url.substringAfterLast('.', "").lowercase()
        }
    }*/

    val cleanUrl = remember(url) { FirebaseUrlHelper.extractDownloadUrl(url) }
    val filename = remember(url) { FirebaseUrlHelper.extractFilename(url) }
    val fileExtension = remember(url) { FirebaseUrlHelper.getFileExtension(url) }


    BackHandler {
        navController.popBackStack()
    }

    // State for loading/error
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Handle different file types
    DisposableEffect(url) {
        when (fileExtension) {
            "pdf" -> {
                // PDF Viewer
                isLoading = false
            }

            "mp4", "mov", "avi" -> {
                // Video Player
                isLoading = false
            }

            "jpg", "jpeg", "png", "gif" -> {
                // Image Viewer
                isLoading = false
            }

            else -> {
                errorMessage = "Unsupported file format"
                isLoading = false
            }
        }

        onDispose { /* Clean up if needed */ }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Course Content") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }

                errorMessage != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            errorMessage ?: "Error loading content",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { /* Retry or go back */ }) {
                            Text("Go Back")
                        }
                    }
                }

                else -> {
                    Log.d("TAG", "ViewContentScreen file extension: $fileExtension")
                    when (fileExtension) {
                        "pdf" -> PDFViewer(url = url)
                        "mp4", "mov", "avi" -> VideoPlayer(url = cleanUrl)
                        "jpg", "jpeg", "png", "gif" -> ImageViewer(url = cleanUrl)
                        else -> DefaultWebView(url = cleanUrl)
                    }
                }
            }
        }
    }
}
