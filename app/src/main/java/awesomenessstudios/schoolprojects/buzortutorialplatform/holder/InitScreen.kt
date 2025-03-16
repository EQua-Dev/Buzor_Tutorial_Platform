package awesomenessstudios.schoolprojects.buzortutorialplatform.holder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.enums.UserRole

@Composable
fun InitScreen(
    holderViewModel: HolderViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    onRoleSelected: (String) -> Unit // Callback to handle role selection
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Teacher Button
        Button(
            onClick = {
                holderViewModel.saveRole(UserRole.TEACHER)
                onRoleSelected("Teacher")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Teacher")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Student Button
        Button(
            onClick = {
                holderViewModel.saveRole(UserRole.STUDENT)
                onRoleSelected("Student") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Student")
        }
    }
}