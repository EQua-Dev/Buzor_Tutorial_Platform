package awesomenessstudios.schoolprojects.buzortutorialplatform.features.common.presentation.createwallet

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.QuestionAnswer
import androidx.compose.material.icons.rounded.QuestionMark
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import awesomenessstudios.schoolprojects.buzortutorialplatform.R
import awesomenessstudios.schoolprojects.buzortutorialplatform.components.AssimOutlinedDropdown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWalletScreen(
    viewModel: CreateWalletViewModel = hiltViewModel(),
    onWalletCreated: (userRole: String) -> Unit
) {
    val state = viewModel.state.collectAsState()
    val context = LocalContext.current
    val securityQuestions = remember {
        listOf(
            "What is your mother's maiden name?" to "What is your mother's maiden name?",
            "What was the name of your first pet?" to "What was the name of your first pet?",
            "What city were you born in?" to "What city were you born in?",
            "What is your favorite book?" to "What is your favorite book?"
        )
    }
    val hashAlgorithms = remember { listOf("Long" to "SHA-256", "Average" to "SHA-512", "Short" to "MD5") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Rounded.Wallet,
                contentDescription = "Create Wallet Icon",
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Secure Your Wallet",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Set up security questions to recover your wallet if needed.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Security Question 1 Dropdown
            AssimOutlinedDropdown(
                label = stringResource(id = R.string.securtiy_question_one_label),
                hint = stringResource(id = R.string.securtiy_question_one_hint),
                options = securityQuestions,
                selectedValue = state.value.securityQuestion1,
                onValueSelected = { viewModel.onEvent(CreateWalletEvent.SecurityQuestion1Changed(it.toString())) },
                isCompulsory = true,
                isSearchable = false,
                /*leadingIcon = { Icon(Icons.Rounded.QuestionMark, contentDescription = "Question 1 Icon") },
                shape = RoundedCornerShape(8.dp)*/
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Answer to Security Question 1
            OutlinedTextField(
                value = state.value.securityAnswer1,
                onValueChange = { viewModel.onEvent(CreateWalletEvent.SecurityAnswer1Changed(it)) },
                label = { Text("Answer to Question 1") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Rounded.QuestionAnswer, contentDescription = "Answer 1 Icon") },
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Security Question 2 Dropdown
            AssimOutlinedDropdown(
                label = stringResource(id = R.string.securtiy_question_two_label),
                hint = stringResource(id = R.string.securtiy_question_one_hint),
                options = securityQuestions,
                selectedValue = state.value.securityQuestion2,
                onValueSelected = { viewModel.onEvent(CreateWalletEvent.SecurityQuestion2Changed(it.toString())) },
                isCompulsory = true,
                isSearchable = false,
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
                /*leadingIcon = { Icon(Icons.Rounded.QuestionMark, contentDescription = "Question 2 Icon") },
                shape = RoundedCornerShape(8.dp)*/
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Answer to Security Question 2
            OutlinedTextField(
                value = state.value.securityAnswer2,
                onValueChange = { viewModel.onEvent(CreateWalletEvent.SecurityAnswer2Changed(it)) },
                label = { Text("Answer to Question 2") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Rounded.QuestionAnswer, contentDescription = "Answer 2 Icon") },
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Wallet Address Complexity Dropdown
            AssimOutlinedDropdown(
                label = stringResource(id = R.string.wallet_address_complexity_label),
                hint = stringResource(id = R.string.wallet_address_complexity_hint),
                options = hashAlgorithms,
                selectedValue = state.value.walletAddressComplexity,
                onValueSelected = {
                    viewModel.onEvent(
                        CreateWalletEvent.WalletAddressComplexityChanged(
                            it.toString()
                        )
                    )
                },
                isCompulsory = true,
                isSearchable = true,
//                leadingIcon = { Icon(Icons.Rounded.Settings, contentDescription = "Complexity Icon") },
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Create Wallet Button
            Button(
                onClick = { viewModel.onEvent(CreateWalletEvent.CreateWallet) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                enabled = !state.value.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (state.value.isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 3.dp)
                } else {
                    Icon(Icons.Rounded.AddCircleOutline, contentDescription = "Create Wallet Icon", modifier = Modifier.padding(end = 8.dp))
                    Text("Create Wallet", style = MaterialTheme.typography.bodyLarge)
                }
            }

            // Loading and Error Handling
            state.value.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            // Navigate on Success
            LaunchedEffect(state.value.isWalletCreated) {
                if (state.value.isWalletCreated) {
                    Toast.makeText(context, "Wallet created successfully!", Toast.LENGTH_SHORT).show()
                    onWalletCreated(state.value.userRole!!)
                }
            }
        }
    }
}