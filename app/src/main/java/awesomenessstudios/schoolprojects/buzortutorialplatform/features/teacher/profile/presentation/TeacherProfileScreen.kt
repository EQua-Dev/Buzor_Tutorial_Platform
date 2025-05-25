package awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.profile.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Star
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.TeacherHomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: TeacherHomeViewModel = hiltViewModel()
) {
    val state = viewModel.state

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (state.errorMessage != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = state.errorMessage ?: "Unknown error",
                color = MaterialTheme.colorScheme.error
            )
        }
        return
    }

    val teacher = state.teacher

    Scaffold(
        /* topBar = {
             TopAppBar(
                 title = { Text("My Profile") },
                 actions = {
                     IconButton(onClick = { viewModel.logOut { navController.popBackStack() } }) {
                         Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                     }
                 }
             )
         }*/
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Image
                AsyncImage(
                    model = teacher.profileImage.ifBlank { "https://via.placeholder.com/150" },
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Name
                Text(
                    text = "${teacher.firstName} ${teacher.lastName}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                // Email
                Text(
                    text = teacher.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Phone Number
                if (teacher.phoneNumber.isNotBlank()) {
                    Text(
                        text = "Phone: ${teacher.phoneNumber}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Subjects
                if (teacher.subjects.isNotEmpty()) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text("Subjects:", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        teacher.subjects.forEach { subject ->
                            Text("• $subject", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Rounded.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFD700)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${teacher.rating}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Verified badge
                if (teacher.isVerified) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Verified",
                            tint = Color(0xFF4CAF50)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Verified Teacher",
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Logout Button
                Button(
                    onClick = { viewModel.logOut { navController.popBackStack() } },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)) // Darker green
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout")
                }
            }
        }

    }
}
