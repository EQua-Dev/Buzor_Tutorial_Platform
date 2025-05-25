package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.courses.coursedetail.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.Result
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.payments.fundwallet.presentation.WithdrawBottomSheet
import awesomenessstudios.schoolprojects.buzortutorialplatform.ui.theme.Typography
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.internal.managers.FragmentComponentManager.findActivity
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentCourseDetailScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    courseId: String,
    viewModel: StudentCourseDetailViewModel = hiltViewModel()
) {

    LaunchedEffect (Unit){
        viewModel.loadCourse(courseId)
    }
    val context = LocalContext.current
    val activity = remember(context) {
        findActivity(context)
            ?.takeIf { it is FragmentActivity } as? FragmentActivity
    }

    val course by viewModel.courseState.collectAsState()
    val teacher by viewModel.teacherState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val showFundingDialog by viewModel.showFundingDialog.collectAsState()

    val scope = rememberCoroutineScope()

    Scaffold(
       /* topBar = {
            TopAppBar(
                title = { Text(course?.title ?: "Course Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }*/
    ) { paddingValues ->
        AnimatedVisibility(
            visible = course != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                course?.let {
                    AsyncImage(
                        model = it.coverImage,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = it.title, style = MaterialTheme.typography.headlineSmall)
                        Text(text = "₦${it.price}", style = MaterialTheme.typography.headlineSmall)
                    }

                    Text(
                        text = it.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    teacher?.let { teacherInfo ->
                        Text(
                            text = "Teacher: ${teacherInfo.firstName} ${teacherInfo.lastName}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedTab == "Content",
                            onClick = { viewModel.onTabSelected("Content") },
                            label = { Text("Content") },
                            leadingIcon = { Icon(Icons.Rounded.Book, contentDescription = "Content") }
                        )
                        FilterChip(
                            selected = selectedTab == "Sessions",
                            onClick = { viewModel.onTabSelected("Sessions") },
                            label = { Text("Sessions") },
                            leadingIcon = { Icon(Icons.Rounded.Event, contentDescription = "Sessions") }
                        )
                    }

                    Crossfade(targetState = selectedTab, label = "tabContent") { tab ->
                        when (tab) {
                            "Content" -> course?.let { contentCourse ->
                                CourseContentView(
                                    course = contentCourse,
                                    currentUserId = currentUserId,
                                    navController = navController,
                                    onTriggerEnroll = {
                                        currentUserId?.let { userId ->
                                            viewModel.checkWalletAndProceed(
                                                userId = userId,
                                                onSufficientFunds = {
                                                    scope.launch {
                                                        activity?.let { fragmentActivity ->
                                                            viewModel.enrollInCourse(fragmentActivity, userId)
                                                        }
                                                    }
                                                },
                                                amount = null, // Price is in the course object
                                                onInsufficientFunds = { viewModel.onSetShowFundingDialog(true) }
                                            )
                                        }
                                    },
                                    onRateTriggered = { rating ->
                                        currentUserId?.let { userId ->
                                            scope.launch {
                                                activity?.let { fragmentActivity ->
                                                    viewModel.rateCourse(
                                                        contentCourse.id,
                                                        userId,
                                                        rating,
                                                        fragmentActivity,
                                                        contentCourse.title
                                                    )
                                                }
                                            }
                                        }
                                    }
                                )
                            }

                            "Sessions" -> course?.let { sessionsCourse ->
                                CourseSessionsView(course = sessionsCourse, viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        } ?: Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        if (showFundingDialog) {
            viewModel.walletState.collectAsState().value?.let { wallet ->
                WithdrawBottomSheet(
                    wallet = wallet,
                    onSuccess = {
                        viewModel.dismissFundingDialog()
                        currentUserId?.let { userId ->
                            viewModel.checkWalletAndProceed(
                                userId = userId,
                                onSufficientFunds = {
                                    scope.launch {
                                        activity?.let { fragmentActivity ->
                                            viewModel.enrollInCourse(fragmentActivity, userId)
                                        }
                                    }
                                },
                                amount = null,
                                onInsufficientFunds = { /* Won't be triggered again */ }
                            )
                        }
                    },
                    onClose = { viewModel.dismissFundingDialog() }
                )
            }
        }
    }
}
