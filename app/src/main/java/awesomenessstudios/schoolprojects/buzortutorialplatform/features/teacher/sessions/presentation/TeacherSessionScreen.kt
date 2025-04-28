package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.sessions.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.common.webview.WebViewScreen
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherSessionScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: TeacherSessionViewModel = hiltViewModel(),
) {
    val state = viewModel.state
    var selectedTab by remember { mutableStateOf(0) }
    val teacherId = FirebaseAuth.getInstance().currentUser?.uid


    val showWebView = remember { mutableStateOf(false) }
    val sessionLink = remember { mutableStateOf("") }


    LaunchedEffect(Unit) {
        if (teacherId != null) {
            viewModel.loadSessions(teacherId)
        }
    }
    if (showWebView.value) {
        WebViewScreen(
            url = sessionLink.value,
            onBack = { showWebView.value = false }
        )
    } else {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Teacher Sessions") },
                    actions = {
                        IconButton(onClick = { /*navController.navigate("history") */}) {
                            Icon(Icons.Default.History, contentDescription = "History")
                        }
                    }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                        Text("Upcoming")
                    }
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                        Text("Requests")
                    }
                }

                when (selectedTab) {
                    0 -> UpcomingSessionsTab(
                        groupSessions = state.groupSessions,
                        singleSessions = state.singleSessions,
                        courseTitles = state.courseTitles,
                        onOpenSession = { link, show ->
                            sessionLink.value = link // 🔥 Save the link
                            showWebView.value = show // 🔥 Trigger WebView
                        }
                    )

                    1 -> SessionRequestsTab(
                        sessionRequests = state.sessionRequests,
                        courseTitles = state.courseTitles
                    ) // To be implemented
                }
            }
        }
    }


}