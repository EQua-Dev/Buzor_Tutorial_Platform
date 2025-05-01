package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.sessions.presentation

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.common.webview.WebViewScreen
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.courses.coursedetail.presentation.StudentCourseDetailViewModel
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.payments.fundwallet.presentation.WithdrawBottomSheet
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.internal.managers.FragmentComponentManager.findActivity
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentSessionScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: StudentSessionViewModel = hiltViewModel(),
    courseDetailViewModel: StudentCourseDetailViewModel = hiltViewModel()
) {
    val state = viewModel.state
    var selectedTab by remember { mutableStateOf(0) }
    val studentId = FirebaseAuth.getInstance().currentUser?.uid

    val amountState = remember { mutableStateOf(0.0) }
    val courseTitleState = remember { mutableStateOf("") }
    val courseIdState = remember { mutableStateOf("") }
    val teacherIdState = remember { mutableStateOf("") }
    val sessionIdState = remember { mutableStateOf("") }


    val showWebView = remember { mutableStateOf(false) }
    val sessionLink = remember { mutableStateOf("") }


    val auth = FirebaseAuth.getInstance()

    val context = LocalContext.current
    val activity = remember(context) {
        findActivity(context)
            ?.takeIf { it is FragmentActivity } as? FragmentActivity
    }

    val scope = rememberCoroutineScope()


    LaunchedEffect(Unit) {
        if (studentId != null) {
            viewModel.loadStudentSessions(studentId)
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
                    title = { Text("Student Sessions") },
                    actions = {
                        IconButton(onClick = { /* future actions */ }) {
                            Icon(Icons.Default.History, contentDescription = "History")
                        }
                    }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                        Text("My Sessions")
                    }
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                        Text("Available")
                    }
                    /*Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                        Text("Requests")
                    }*/
                }

                when (selectedTab) {
                    0 -> MySessionsTab(
                        mySessions = state.myGroupSessions + state.mySingleSessions,
                        courseTitles = state.courseTitles,
                        onOpenSession = { link, show ->
                            sessionLink.value = link // 🔥 Save the link
                            showWebView.value = show // 🔥 Trigger WebView
                        }
                    )

                    1 -> AvailableSessionsTab(
                        availableSessions = state.availableGroupSessions,
                        courseTitles = state.courseTitles,
                        onJoin = { amount, courseTitle, courseId, teacherId, sessionId ->
                            amountState.value = amount
                            courseTitleState.value = courseTitle
                            courseIdState.value = courseId
                            teacherIdState.value = teacherId
                            sessionIdState.value = sessionId
                            courseDetailViewModel.checkWalletAndProceed(
                                userId = auth.currentUser!!.uid,
                                onSufficientFunds = {
                                    scope.launch {
                                        activity?.let { fragmentActivity ->
                                            viewModel.joinGroupSession(
                                                courseTitle,
                                                courseId,
                                                sessionId,
                                                teacherId,
                                                auth.currentUser!!.uid,
                                                fragmentActivity,
                                                amount
                                            )
                                        } ?: run {
                                            // Handle case where activity isn't available
                                            // Maybe show error or use alternative authentication
                                        }

                                    }
                                },
                                amount = amount,
                                onInsufficientFunds = {
                                    // Dialog state is handled in viewModel


                                }
                            )
                        }
                    )
                    /* 2 -> RequestsTab(
                         requests = state.requests,
                         courseTitles = state.courseTitles
                     )*/
                }
            }
        }

    }
    if (courseDetailViewModel.showFundingDialog) {
        courseDetailViewModel.walletState?.let { it1 ->
            WithdrawBottomSheet(
                wallet = it1,
                onSuccess = {
                    courseDetailViewModel.dismissFundingDialog()
                    auth.currentUser!!.uid?.let {
                        courseDetailViewModel.checkWalletAndProceed(
                            userId = it,
                            onSufficientFunds = {
                                scope.launch {
                                    activity?.let { fragmentActivity ->
                                        viewModel.joinGroupSession(
                                            courseTitleState.value,
                                            courseIdState.value,
                                            sessionIdState.value,
                                            teacherIdState.value,
                                            auth.currentUser!!.uid,
                                            fragmentActivity,
                                            amountState.value
                                        )
                                    } ?: run {
                                        // Handle case where activity isn't available
                                        // Maybe show error or use alternative authentication
                                    }
                                }
                            }, amount = amountState.value,
                            onInsufficientFunds = { /* this won't be triggered again here */ }
                        )
                    }
                }, onClose = ({ courseDetailViewModel.dismissFundingDialog() })
            )
        }
    }

}
