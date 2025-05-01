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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.payments.fundwallet.presentation.WithdrawBottomSheet
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.sessions.presentation.SessionCard
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.LoadingDialog
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.getDate
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.internal.managers.FragmentComponentManager.findActivity
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun CourseSessionsView(course: Course, viewModel: StudentCourseDetailViewModel = hiltViewModel()) {
    val sessions = viewModel.groupSessionsState
    val auth = FirebaseAuth.getInstance()

    val context = LocalContext.current
    val activity = remember(context) {
        findActivity(context)
            ?.takeIf { it is FragmentActivity } as? FragmentActivity
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (course.id != "") {
            viewModel.loadSessions(course.id)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "${sessions?.size ?: 0} sessions")
            if (course.privateSessionPrice != "")
                Button(onClick = { viewModel.onRequestSessionClicked() }) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Request Private Session",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(text = "₦${course.privateSessionPrice}")
                    }
                }
        }


    }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)

    ) {

        if (sessions.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Text(
                        text = "This course has no upcoming group sessions.",
                        textAlign = TextAlign.Center
                    )

                    if (course.privateSessionPrice != "")
                        Text(
                            text = "You can request a private one.",
                            textAlign = TextAlign.Center
                        )
                }

            }

        } else {
            sessions.forEach { session ->
                CourseSessionCard(
                    isGroup = true,
                    date = getDate(session.startTime.trim().toLong(), "EEE, dd MMM yyyy"),
                    time = getDate(session.startTime.trim().toLong(), "hh:mm a"),
                    price = session.price,
                    typeIcon = Icons.Default.Groups,
                    typeText = "${(session.maxAttendance.minus(session.students.size))} of ${session.maxAttendance}\nseats left"
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            /*items(sessions) { session ->
                CourseSessionCard(
                    isGroup = true,
                    date = getDate(session.startTime.trim().toLong(), "EEE, dd MMM yyyy"),
                    time = getDate(session.startTime.trim().toLong(), "hh:mm a"),
                    price = session.price,
                    typeIcon = Icons.Default.Groups,
                    typeText = "${(session.maxAttendance.minus(session.students.size))} of ${session.maxAttendance}\nseats left"
                )
                Spacer(modifier = Modifier.height(8.dp))
            }*/
        }
    }

    if (viewModel.showRequestDialog) {
        RequestPrivateSessionDialog(
            data = viewModel.newSessionData,
            onDismiss = { viewModel.onDismissDialog() },
            onConfirm = {
                viewModel.checkWalletAndProceed(
                    userId = auth.currentUser!!.uid,
                    onSufficientFunds = {
                        scope.launch {
                            activity?.let { fragmentActivity ->
                                viewModel.createSingleSession(
                                    course.id,
                                    course.ownerId,
                                    fragmentActivity,
                                    course.privateSessionPrice.toDouble()
                                )
                            } ?: run {
                                // Handle case where activity isn't available
                                // Maybe show error or use alternative authentication
                            }

                        }
                    },
                    amount = course.privateSessionPrice.toDouble(),
                    onInsufficientFunds = {
                        // Dialog state is handled in viewModel


                    }
                )
            },
            onValueChange = { updatedData -> viewModel.onUpdateNewSessionData(updatedData) }
        )
    }

    if (viewModel.showFundingDialog) {
        viewModel.walletState?.let { it1 ->
            WithdrawBottomSheet(
                wallet = it1,
                onSuccess = {
                    viewModel.dismissFundingDialog()
                    auth.currentUser!!.uid?.let {
                        viewModel.checkWalletAndProceed(
                            userId = it,
                            onSufficientFunds = {
                                scope.launch {
                                    activity?.let { fragmentActivity ->
                                        viewModel.createSingleSession(
                                            course.id,
                                            course.ownerId,
                                            fragmentActivity,
                                            course.privateSessionPrice.toDouble()
                                        )
                                    } ?: run {
                                        // Handle case where activity isn't available
                                        // Maybe show error or use alternative authentication
                                    }
                                }
                            }, amount = course.privateSessionPrice.toDouble(),
                            onInsufficientFunds = { /* this won't be triggered again here */ }
                        )
                    }
                }, onClose = ({ viewModel.dismissFundingDialog() })
            )
        }
    }

}

@Composable
fun CourseSessionCard(
    isGroup: Boolean,
    date: String,
    time: String,
    price: String,
    typeIcon: ImageVector,
    typeText: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(0.7f)) {
                    Text(text = "Date: $date")
                    Text(text = "Time: $time")
                    Text(text = "Price: ₦$price")
                }
                Divider(
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(4.dp)
                        .padding(horizontal = 4.dp)
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(0.3f)
                ) {
                    Icon(imageVector = typeIcon, contentDescription = null, Modifier.size(32.dp))
                    Text(text = typeText)
                    Button(
                        modifier = Modifier.clip(shape = RoundedCornerShape(12.dp)),
                        onClick = { /*TODO*/ }) {
                        Text(text = "Join")
                    }
                }
            }
        }
    }

}
