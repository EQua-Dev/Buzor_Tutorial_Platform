package awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.wallet.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun StudentWalletScreen(modifier: Modifier = Modifier, navController: NavHostController,) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Text(text = "Student Wallet Screen")
    }
}