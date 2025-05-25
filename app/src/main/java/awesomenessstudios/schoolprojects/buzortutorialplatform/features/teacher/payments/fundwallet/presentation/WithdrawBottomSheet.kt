package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.payments.fundwallet.presentation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.models.Wallet
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.wallet.fundwallet.presentation.WithdrawViewModel


@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun WithdrawBottomSheet(
    title: String = "Top-up Your Wallet",
    wallet: Wallet,
    viewModel: WithdrawViewModel = hiltViewModel(),
    onClose: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val activity = remember(context) {
        context.findActivity()?.takeIf { it is FragmentActivity } as? FragmentActivity
    }
    val state = viewModel.state
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = state.amount,
                    onValueChange = viewModel::onAmountChange,
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.answer1,
                    onValueChange = viewModel::onQuestion1Change,
                    label = { Text(wallet.securityQuestion1) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = state.answer2,
                    onValueChange = viewModel::onQuestion2Change,
                    label = { Text(wallet.securityQuestion2) },
                    modifier = Modifier.fillMaxWidth()
                )

                if (state.error != null) {
                    Text(
                        state.error ?: "",
                        color = Color.Red,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        activity?.let { fragmentActivity ->
                            viewModel.verifyAndWithdraw(
                                activity = fragmentActivity,
                                wallet = wallet,
                                onSuccess = onSuccess,
                                onFailure = { error -> viewModel.state = state.copy(error = error) }
                            )
                        } ?: run {
                            // Handle case where activity isn't available
                            // Maybe show error or use alternative authentication
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continue")
                }

                TextButton(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}


// Helper extension function
fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    return null
}