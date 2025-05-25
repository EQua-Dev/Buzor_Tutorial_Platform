package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.courses.coursedetail.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.rounded.EventNote
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.Result
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Course
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.courses.coursedetail.components.RequestPrivateSessionDialog
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.sessions.presentation.StudentSessionViewModel
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.payments.fundwallet.presentation.WithdrawBottomSheet
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.sessions.presentation.SessionCard
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.LoadingDialog
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.getDate
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.internal.managers.FragmentComponentManager.findActivity
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun CourseSessionsView(
    course: Course,
    viewModel: StudentCourseDetailViewModel = hiltViewModel(),
    sessionViewModel: StudentSessionViewModel = hiltViewModel(),
) {
    val sessions by viewModel.groupSessionsState.collectAsState()
    val auth = FirebaseAuth.getInstance()

    val context = LocalContext.current
    val activity = remember(context) {
        findActivity(context)
            ?.takeIf { it is FragmentActivity } as? FragmentActivity
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(course.id) {
        viewModel.loadSessions(course.id)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${sessions?.size ?: 0} upcoming session(s)",
                style = MaterialTheme.typography.titleMedium
            )
            if (course.privateSessionPrice.isNotBlank() && course.privateSessionPrice != "0.0") {
                Button(
                    onClick = { viewModel.onsetShowRequestDialog(true) },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Request Private Session",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "₦${course.privateSessionPrice}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))



        if (sessions.isNullOrEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Rounded.EventNote,
                        contentDescription = "No Sessions",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No upcoming group sessions available for this course.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (course.privateSessionPrice.isNotBlank() && course.privateSessionPrice != "0.0") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "You can request a private session.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }


        } else {
            sessions!!.forEach { session ->
                CourseSessionCard(
                    isGroup = true,
                    date = getDate(session.startTime.trim().toLong(), "EEE, dd MMM yyyy"),
                    time = getDate(session.startTime.trim().toLong(), "hh:mm a"),
                    price = session.price,
                    typeIcon = Icons.Filled.Groups,
                    typeText = "${(session.maxAttendance.minus(session.students.size))} / ${session.maxAttendance} seats left",
                    enableJoin = !session.students.contains(auth.currentUser!!.uid),
                    onJoinClick = {
                        viewModel.checkWalletAndProceed(
                            userId = auth.currentUser!!.uid,
                            onSufficientFunds = {
                                scope.launch {
                                    activity?.let { fragmentActivity ->
                                        sessionViewModel.joinGroupSession(
                                            course.title,
                                            course.id,
                                            session.id,
                                            course.ownerId,
                                            auth.currentUser!!.uid,
                                            fragmentActivity,
                                            session.price.toDouble()
                                        )
                                    } ?: run {
                                        // Handle case where activity isn't available
                                        // Maybe show error or use alternative authentication
                                    }

                                }
                            },
                            amount = session.price.toDouble(),
                            onInsufficientFunds = {
                                // Dialog state is handled in viewModel


                            }
                        )
                    }
                )
            }

        }

    }

    if (viewModel.showRequestDialog.collectAsState().value) {
        RequestPrivateSessionDialog(
            data = viewModel.newSessionData.collectAsState().value,
            onDismiss = { viewModel.onsetShowRequestDialog(false) },
            onConfirm = {
                viewModel.onsetShowRequestDialog(false)
                auth.currentUser?.uid?.let { userId ->
                    viewModel.checkWalletAndProceed(
                        userId = userId,
                        onSufficientFunds = {
                            viewModel.onsetShowRequestDialog(false)
                            scope.launch {
                                activity?.let { fragmentActivity ->
                                    viewModel.createSingleSession(
                                        course.id,
                                        course.ownerId,
                                        fragmentActivity,
                                        course.privateSessionPrice.toDouble()
                                    )
                                }
                            }
                        },
                        amount = course.privateSessionPrice.toDouble(),
                        onInsufficientFunds = { viewModel.onSetShowFundingDialog(true) }
                    )
                }
            },
            onValueChange = { updatedData -> viewModel.onUpdateNewSessionData(updatedData) }
        )
    }

    if (viewModel.showFundingDialog.collectAsState().value) {
        viewModel.walletState.collectAsState().value?.let { wallet ->
            WithdrawBottomSheet(
                wallet = wallet,
                onSuccess = {
                    viewModel.dismissFundingDialog()
                    auth.currentUser?.uid?.let { userId ->
                        viewModel.checkWalletAndProceed(
                            userId = userId,
                            onSufficientFunds = {
                                scope.launch {
                                    activity?.let { fragmentActivity ->
                                        viewModel.createSingleSession(
                                            course.id,
                                            course.ownerId,
                                            fragmentActivity,
                                            course.privateSessionPrice.toDouble()
                                        )
                                    }
                                }
                            },
                            amount = course.privateSessionPrice.toDouble(),
                            onInsufficientFunds = { /* Won't be triggered again */ }
                        )
                    }
                },
                onClose = { viewModel.dismissFundingDialog() }
            )
        }
    }
}

@Composable
fun CourseSessionCard(
    enableJoin: Boolean,
    isGroup: Boolean,
    date: String,
    time: String,
    price: String,
    typeIcon: ImageVector,
    typeText: String,
    onJoinClick: () -> Unit // Added callback for join action
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(0.7f)) {
                Text(text = "Date: $date", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Time: $time", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Price: ₦$price", style = MaterialTheme.typography.bodyMedium)
            }
            Divider(
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .padding(horizontal = 8.dp)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(0.3f)
            ) {
                Icon(imageVector = typeIcon, contentDescription = null, Modifier.size(32.dp))
                Text(
                    text = typeText,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onJoinClick,
                    shape = RoundedCornerShape(8.dp),
                    enabled = isGroup && enableJoin // Enable join button for group sessions
                ) {
                    Text(text = "Join")
                }
            }
        }
    }
}


