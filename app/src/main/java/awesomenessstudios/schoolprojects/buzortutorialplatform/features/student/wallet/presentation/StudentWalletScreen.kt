package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.wallet.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.wallet.fundwallet.presentation.FundingBottomSheet
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.payments.fundwallet.presentation.WithdrawBottomSheet
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.payments.presentation.TeacherPaymentsViewModel
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.payments.presentation.TeacherPaymentsEvent
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.getDate
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentWalletScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: StudentWalletViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(Unit) {
        currentUserId?.let { viewModel.loadWallet(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp) // Add vertical spacing between main sections
    ) {
        // Balance Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // Use space between for better distribution
            ) {
                Column {
                    Text("Balance", style = MaterialTheme.typography.labelMedium)
                    if (state.isBalanceVisible) {
                        Text(
                            "₦${state.balance}",
                            style = MaterialTheme.typography.headlineLarge // Make balance more prominent
                        )
                    } else {
                        Text("••••••", style = MaterialTheme.typography.headlineLarge)
                    }
                }
                IconButton(onClick = { viewModel.onEvent(WalletEvent.ToggleBalanceVisibility) }) {
                    Icon(
                        imageVector = if (state.isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (state.isBalanceVisible) "Hide balance" else "Show balance"
                    )
                }
                Button(onClick = { viewModel.showFundingDialog() }) {
                    Text("Fund Wallet")
                }
            }
        }

        // Transaction History Section
        Column {
            Text("Transaction History", style = MaterialTheme.typography.titleLarge)
            Row(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = state.filter == "All",
                    onClick = { viewModel.onEvent(WalletEvent.OnFilterChange("All")) },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = state.filter == "Credit",
                    onClick = { viewModel.onEvent(WalletEvent.OnFilterChange("Credit")) },
                    label = { Text("Credit") },
                    leadingIcon = { Icon(Icons.Rounded.ArrowUpward, contentDescription = "Credit") } // Example icon
                )
                FilterChip(
                    selected = state.filter == "Debit",
                    onClick = { viewModel.onEvent(WalletEvent.OnFilterChange("Debit")) },
                    label = { Text("Debit") },
                    leadingIcon = { Icon(Icons.Rounded.ArrowDownward, contentDescription = "Debit") } // Example icon
                )
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                val filteredHistory = when (state.filter) {
                    "Credit" -> state.history.filter { it.transactionType == "credit" }
                    "Debit" -> state.history.filter { it.transactionType == "debit" }
                    else -> state.history
                }

                if (filteredHistory.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No transactions to display based on the current filter.", textAlign = TextAlign.Center)
                        }
                    }
                } else {
                    items(filteredHistory) { transaction ->
                        ElevatedCard( // Use ElevatedCard for a subtle visual lift
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { viewModel.onEvent(WalletEvent.OnTransactionClick(transaction)) },
                            shape = RoundedCornerShape(8.dp) // Add rounded corners for a softer look
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        getDate(
                                            transaction.dateCreated.trim().toLong(),
                                            "EEE, dd MMM yyyy | hh:mm a" // Standard date format
                                        ),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = transaction.transactionType.capitalize(),
                                        color = if (transaction.transactionType == "credit") Color.Green else Color.Red,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(transaction.description, style = MaterialTheme.typography.bodyLarge)
                                Spacer(modifier = Modifier.height(8.dp))
                                val prefix = if (transaction.transactionType == "credit") "+" else "-"
                                Text(
                                    text = "$prefix₦${transaction.amount}",
                                    color = if (transaction.transactionType == "credit") Color.Green else Color.Red,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Transaction Details Dialog
    state.selectedTransaction?.let { transaction ->
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(WalletEvent.OnDismissDialog) },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(WalletEvent.OnDismissDialog) }) {
                    Text("Close")
                }
            },
            title = { Text("Transaction Details") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Type: ${transaction.transactionType.capitalize()}")
                    Text("Amount: ₦${transaction.amount}")
                    Text("Description: ${transaction.description}")
                    Text("Sender: ${transaction.sender}")
                    Text("Receiver: ${transaction.receiver}")
                    Text("Location: ${transaction.transactionLocation}")
                    Text("Date: ${getDate(transaction.dateCreated.trim().toLong(), "EEE, dd MMM yyyy | hh:mm:ss a")}") // More detailed date
                }
            }
        )
    }

    // Funding Bottom Sheet
    if (state.showFundingDialog) {
        state.walletState?.let { wallet ->
            FundingBottomSheet(
                wallet = wallet,
                onSuccess = { viewModel.dismissFundingDialog() },
                onClose = { viewModel.dismissFundingDialog() }
            )
        }
    }

  /*  // Loading State Handling (Example)
    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    // Error State Handling (Example)
    state.error?.let { errorMessage ->
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(WalletEvent.ClearError) },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(WalletEvent.ClearError) }) {
                    Text("Okay")
                }
            },
            title = { Text("Error") },
            text = { Text(errorMessage) }
        )
    }*/
}