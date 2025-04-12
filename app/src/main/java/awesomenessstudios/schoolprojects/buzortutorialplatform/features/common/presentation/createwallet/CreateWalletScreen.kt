package awesomenessstudios.schoolprojects.buzortutorialplatform.features.common.presentation.createwallet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import awesomenessstudios.schoolprojects.buzortutorialplatform.R
import awesomenessstudios.schoolprojects.buzortutorialplatform.components.AssimOutlinedDropdown
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.auth.presentation.signup.TeacherRegistrationEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWalletScreen(
    viewModel: CreateWalletViewModel = hiltViewModel(),
    onWalletCreated: (userRole: String) -> Unit
) {
    val state = viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Security Question 1 Dropdown
        var expanded1 by remember { mutableStateOf(false) }
        val securityQuestions = listOf(
            "What is your mother's maiden name?" to "What is your mother's maiden name?",
            "What was the name of your first pet?" to "What was the name of your first pet?",
            "What city were you born in?" to "What city were you born in?",
            "What is your favorite book?" to "What is your favorite book?"
        )

//        val hashAlgorithms = listOf("Long" to "SHA-256", "Average" to "SHA-512", "Short" to "MD5")
        AssimOutlinedDropdown(
            label = stringResource(id = R.string.securtiy_question_one_label),
            hint = stringResource(id = R.string.securtiy_question_one_hint),
            options = securityQuestions,
            selectedValue = state.value.walletAddressComplexity,
            onValueSelected = { viewModel.onEvent(CreateWalletEvent.SecurityQuestion1Changed(it.toString())) },
            isCompulsory = true,
//            error = state.genderError?.let { stringResource(id = it) },
            isSearchable = false // Enable search functionality
        )
        /* ExposedDropdownMenuBox(
             expanded = expanded1,
             onExpandedChange = { expanded1 = !expanded1 }
         ) {
             OutlinedTextField(
                 value = state.securityQuestion1,
                 onValueChange = {},
                 label = { Text("Security Question 1") },
                 modifier = Modifier.fillMaxWidth(),
                 readOnly = true,
                 trailingIcon = {
                     ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded1)
                 }
             )

             ExposedDropdownMenu(
                 expanded = expanded1,
                 onDismissRequest = { expanded1 = false }
             ) {
                 securityQuestions.forEach { question ->
                     DropdownMenuItem(
                         onClick = {
                             viewModel.onEvent(CreateWalletEvent.SecurityQuestion1Changed(question))
                             expanded1 = false
                         }, text = {Text(text = question)}
                     )
                 }
             }
         }*/

        Spacer(modifier = Modifier.height(8.dp))

        // Answer to Security Question 1
        OutlinedTextField(
            value = state.value.securityAnswer1,
            onValueChange = { viewModel.onEvent(CreateWalletEvent.SecurityAnswer1Changed(it)) },
            label = { Text("Answer to Security Question 1") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Security Question 2 Dropdown
        var expanded2 by remember { mutableStateOf(false) }

        AssimOutlinedDropdown(
            label = stringResource(id = R.string.securtiy_question_two_label),
            hint = stringResource(id = R.string.securtiy_question_one_hint),
            options = securityQuestions,
            selectedValue = state.value.walletAddressComplexity,
            onValueSelected = { viewModel.onEvent(CreateWalletEvent.SecurityQuestion2Changed(it.toString())) },
            isCompulsory = true,
//            error = state.genderError?.let { stringResource(id = it) },
            isSearchable = false // Enable search functionality
        )

        /* ExposedDropdownMenuBox(
             expanded = expanded2,
             onExpandedChange = { expanded2 = !expanded2 }
         ) {
             OutlinedTextField(
                 value = state.securityQuestion2,
                 onValueChange = {},
                 label = { Text("Security Question 2") },
                 modifier = Modifier.fillMaxWidth(),
                 readOnly = true,
                 trailingIcon = {
                     ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded2)
                 }
             )

             ExposedDropdownMenu(
                 expanded = expanded2,
                 onDismissRequest = { expanded2 = false }
             ) {
                 securityQuestions.forEach { question ->
                     DropdownMenuItem(
                         onClick = {
                             viewModel.onEvent(CreateWalletEvent.SecurityQuestion2Changed(question))
                             expanded2 = false
                         },
                         text = {Text(text = question)}
                     )
                 }
             }
         }*/

        Spacer(modifier = Modifier.height(8.dp))

        // Answer to Security Question 2
        OutlinedTextField(
            value = state.value.securityAnswer2,
            onValueChange = { viewModel.onEvent(CreateWalletEvent.SecurityAnswer2Changed(it)) },
            label = { Text("Answer to Security Question 2") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Wallet Address Complexity Dropdown
        var expanded3 by remember { mutableStateOf(false) }
        val hashAlgorithms = listOf("Long" to "SHA-256", "Average" to "SHA-512", "Short" to "MD5")
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
//            error = state.genderError?.let { stringResource(id = it) },
            isSearchable = true // Enable search functionality
        )
        /*

                ExposedDropdownMenuBox(
                    expanded = expanded3,
                    onExpandedChange = { expanded3 = !expanded3 }
                ) {
                    OutlinedTextField(
                        value = state.walletAddressComplexity,
                        onValueChange = {},
                        label = { Text("Wallet Address Complexity") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded3)
                        }
                    )

                    ExposedDropdownMenu(
                        expanded = expanded3,
                        onDismissRequest = { expanded3 = false }
                    ) {
                        hashAlgorithms.forEach { algorithm ->
                            DropdownMenuItem(
                                onClick = {
                                    viewModel.onEvent(CreateWalletEvent.WalletAddressComplexityChanged(algorithm))
                                    expanded3 = false
                                },
                                text = {Text(text = algorithm)}
                            )
                        }
                    }
                }
        */

        Spacer(modifier = Modifier.height(16.dp))

        // Create Wallet Button
        Button(
            onClick = { viewModel.onEvent(CreateWalletEvent.CreateWallet) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Wallet")
        }

        // Loading and Error Handling
        if (state.value.isLoading) {
            CircularProgressIndicator()
        }

        state.value.errorMessage?.let { error ->
            Text(text = error, color = Color.Red)
        }

        // Navigate on Success
        LaunchedEffect(state.value.isWalletCreated) {
            if (state.value.isWalletCreated) {
                onWalletCreated(state.value.userRole!!)
            }
        }
    }
}