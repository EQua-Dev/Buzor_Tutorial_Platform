package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.payments.presentation

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
import androidx.compose.material3.CircularProgressIndicator
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
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.payments.fundwallet.presentation.WithdrawBottomSheet
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.getDate
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherPaymentsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: TeacherPaymentsViewModel = hiltViewModel()
) {
    val state = viewModel.state//.value
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(currentUserId) {
        currentUserId?.let { viewModel.loadWallet(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Balance", style = MaterialTheme.typography.labelMedium)
                    if (state.isBalanceVisible) {
                        Text("₦${state.balance}", style = MaterialTheme.typography.headlineLarge)
                    } else {
                        Text("••••••", style = MaterialTheme.typography.headlineLarge)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.onEvent(TeacherPaymentsEvent.ToggleBalanceVisibility) }) {
                        Icon(
                            imageVector = if (state.isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (state.isBalanceVisible) "Hide balance" else "Show balance"
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = { viewModel.showFundingDialog() }) {
                        Text("Withdraw Funds")
                    }
                }
            }
        }

        // Transaction History
        Column {
            Text("Transaction History", style = MaterialTheme.typography.titleLarge)
            Row(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = state.filter == "All",
                    onClick = { viewModel.onEvent(TeacherPaymentsEvent.OnFilterChange("All")) },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = state.filter == "Credit",
                    onClick = { viewModel.onEvent(TeacherPaymentsEvent.OnFilterChange("Credit")) },
                    label = { Text("Credit") },
                    leadingIcon = { Icon(Icons.Rounded.ArrowUpward, contentDescription = "Credit") }
                )
                FilterChip(
                    selected = state.filter == "Debit",
                    onClick = { viewModel.onEvent(TeacherPaymentsEvent.OnFilterChange("Debit")) },
                    label = { Text("Debit") },
                    leadingIcon = { Icon(Icons.Rounded.ArrowDownward, contentDescription = "Debit") }
                )
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                val filteredHistory = when (state.filter) {
                    "Credit" -> state.history.filter { it.transactionType.lowercase() == "credit" }
                    "Debit" -> state.history.filter { it.transactionType.lowercase() == "debit" }
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
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { viewModel.onEvent(TeacherPaymentsEvent.OnTransactionClick(transaction)) },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = getDate(transaction.dateCreated.toLong(), "EEE dd/MM/yyyy | hh:mm a"),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    val typeColor =
                                        if (transaction.transactionType.lowercase() == "credit") Color.Green else Color.Red
                                    Text(
                                        text = transaction.transactionType.replaceFirstChar { it.uppercase() },
                                        color = typeColor,
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = transaction.description, style = MaterialTheme.typography.bodyLarge)
                                Spacer(modifier = Modifier.height(8.dp))
                                val amountColor =
                                    if (transaction.transactionType.lowercase() == "credit") Color.Green else Color.Red
                                val prefix = if (transaction.transactionType.lowercase() == "credit") "+" else "-"
                                Text(
                                    text = "$prefix₦${transaction.amount}",
                                    color = amountColor,
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
            onDismissRequest = { viewModel.onEvent(TeacherPaymentsEvent.OnDismissDialog) },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(TeacherPaymentsEvent.OnDismissDialog) }) {
                    Text("Close")
                }
            },
            title = { Text("Transaction Details") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Type: ${transaction.transactionType.replaceFirstChar { it.uppercase() }}")
                    Text("Amount: ₦${transaction.amount}")
                    Text("Description: ${transaction.description}")
                    Text("Sender: ${transaction.sender}")
                    Text("Receiver: ${transaction.receiver}")
                    Text("Location: ${transaction.transactionLocation}")
                    Text("Date: ${getDate(transaction.dateCreated.toLong(), "EEE, dd MMM yyyy")}") // Consistent date format
                    Text("Time: ${getDate(transaction.dateCreated.toLong(), "hh:mm a")}")       // Consistent time format
                }
            }
        )
    }

    // Withdraw Funds Bottom Sheet
    if (state.showFundingDialog) {
        state.walletState?.let { wallet ->
            WithdrawBottomSheet(
                title = "Withdraw Funds",
                wallet = wallet,
                onSuccess = { viewModel.dismissFundingDialog() },
                onClose = { viewModel.dismissFundingDialog() }
            )
        }
    }

    // Loading State Handling (Example)
    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    // Error State Handling (Example)
    state.error?.let { errorMessage ->
        AlertDialog(
            onDismissRequest = { viewModel.state.copy(error = null) },
            confirmButton = {
                TextButton(onClick = { viewModel.state.copy(error = null) }) {
                    Text("Okay")
                }
            },
            title = { Text("Error") },
            text = { Text(errorMessage) }
        )
    }
}