package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.courses.coursedetail.presentation

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
@Composable
fun StudentCourseDetailScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    courseId: String,
    viewModel: StudentCourseDetailViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val activity = remember(context) {
        findActivity(context)
            ?.takeIf { it is FragmentActivity } as? FragmentActivity
    }

    val course = viewModel.courseState
    val teacher = viewModel.teacherState
    val selectedTab = viewModel.selectedTab
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val showFundingDialog = viewModel.showFundingDialog

    val scope = rememberCoroutineScope()



    LaunchedEffect(courseId) {
        viewModel.loadCourse(courseId)
    }

    course?.let {
        Column(modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
        ) {
            AsyncImage(
                model = it.coverImage,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = it.title, style = MaterialTheme.typography.titleMedium)
                Text(text = "₦${it.price}", style = MaterialTheme.typography.titleMedium)
            }

            Text(
                text = it.description,
                style = Typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            teacher?.let {
                Text(
                    text = "Teacher: ${it.firstName} ${it.lastName}",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Content", "Sessions").forEach { tab ->
                    FilterChip(
                        selected = selectedTab == tab,
                        onClick = { viewModel.onTabSelected(tab) },
                        label = { Text(tab) }
                    )
                }
            }

            when (selectedTab) {
                "Content" -> CourseContentView(
                    course = it,
                    currentUserId = currentUserId,
                    navController = navController,
                    onTriggerEnroll = {
                        if (currentUserId != null) {
                            viewModel.checkWalletAndProceed(
                                userId = currentUserId,
                                onSufficientFunds = {
                                    scope.launch {
                                        activity?.let { fragmentActivity ->
                                            when (val result = viewModel.enrollInCourse(
                                                fragmentActivity,
                                                currentUserId
                                            )) {
                                                is Result.Success -> {
                                                    // Handle success (e.g., show message)
                                                }

                                                is Result.Failure -> {
                                                    // Handle error (e.g., show error message)
                                                }
                                            }
                                        } ?: run {
                                            // Handle case where activity isn't available
                                            // Maybe show error or use alternative authentication
                                        }

                                    }
                                },
                                amount = null,
                                onInsufficientFunds = {
                                    // Dialog state is handled in viewModel


                                }
                            )
                        }
                    })

                "Sessions" -> CourseSessionsView(course = it)
            }
        }
        if (showFundingDialog) {
            viewModel.walletState?.let { it1 ->
                WithdrawBottomSheet(
                    wallet = it1,
                    onSuccess = {
                        viewModel.dismissFundingDialog()
                        currentUserId?.let {
                            viewModel.checkWalletAndProceed(
                                userId = it,
                                onSufficientFunds = {
                                    scope.launch {
                                        activity?.let { fragmentActivity ->
                                            when (val result = viewModel.enrollInCourse(
                                                fragmentActivity,
                                                currentUserId
                                            )) {
                                                is Result.Success -> {
                                                    // Handle success (e.g., show message)
                                                }

                                                is Result.Failure -> {
                                                    // Handle error (e.g., show error message)
                                                }
                                            }
                                        } ?: run {
                                            // Handle case where activity isn't available
                                            // Maybe show error or use alternative authentication
                                        }
                                    }
                                }, amount = null,
                                onInsufficientFunds = { /* this won't be triggered again here */ }
                            )
                        }
                    }, onClose = ({ viewModel.dismissFundingDialog() })
                )
            }
        }
    } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
