package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.payments.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.payments.fundwallet.presentation.WithdrawBottomSheet
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun TeacherPaymentsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: TeacherPaymentsViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(Unit) {
        currentUserId?.let { viewModel.loadWallet(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Balance", style = MaterialTheme.typography.labelMedium)
                    if (state.isBalanceVisible) {
                        Text("₦${state.balance}", style = MaterialTheme.typography.headlineSmall)
                    } else {
                        Text("••••••", style = MaterialTheme.typography.headlineSmall)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.onEvent(TeacherPaymentsEvent.ToggleBalanceVisibility) }) {
                        Icon(
                            imageVector = if (state.isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        viewModel.showFundingDialog()
                    }) {
                        Text("Fund Wallet")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Transaction History", style = MaterialTheme.typography.titleMedium)

        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            listOf("All", "Credit", "Debit").forEach { filter ->
                FilterChip(
                    selected = state.filter == filter,
                    onClick = { viewModel.onEvent(TeacherPaymentsEvent.OnFilterChange(filter)) },
                    label = { Text(filter) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            val filteredHistory = when (state.filter) {
                "Credit" -> state.history.filter { it.transactionType == "credit" }
                "Debit" -> state.history.filter { it.transactionType == "debit" }
                else -> state.history
            }

            items(filteredHistory) { transaction ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { viewModel.onEvent(TeacherPaymentsEvent.OnTransactionClick(transaction)) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = transaction.dateCreated)
                            Text(text = transaction.transactionType.capitalize())
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(transaction.description)
                            val amountColor =
                                if (transaction.transactionType == "credit") Color.Green else Color.Red
                            val prefix = if (transaction.transactionType == "credit") "+" else "-"
                            Text(
                                text = "$prefix₦${transaction.amount}",
                                color = amountColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    state.selectedTransaction?.let { transaction ->
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(TeacherPaymentsEvent.OnDismissDialog) },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(TeacherPaymentsEvent.OnDismissDialog) }) {
                    Text("Close")
                }
            },
            title = { Text("Transaction Details") },
            text = {
                Column {
                    Text("Type: ${transaction.transactionType}")
                    Text("Amount: ₦${transaction.amount}")
                    Text("Description: ${transaction.description}")
                    Text("Sender: ${transaction.sender}")
                    Text("Receiver: ${transaction.receiver}")
                    Text("Location: ${transaction.transactionLocation}")
                    Text("Date: ${transaction.dateCreated}")
                }
            }
        )
    }
    if (state.showFundingDialog) {
        state.walletState?.let { it1 ->

            WithdrawBottomSheet(
                wallet = it1,
                onSuccess = {
                    viewModel.dismissFundingDialog()

                }, onClose = ({ viewModel.dismissFundingDialog() })
            )
        }
    }
}