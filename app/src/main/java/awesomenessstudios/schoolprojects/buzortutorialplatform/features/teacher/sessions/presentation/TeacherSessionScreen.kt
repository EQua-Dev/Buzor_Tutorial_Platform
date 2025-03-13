package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.sessions.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun TeacherSessionScreen(modifier: Modifier = Modifier, navController: NavHostController,) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Text(text = "Teacher Session Screen")
    }
}