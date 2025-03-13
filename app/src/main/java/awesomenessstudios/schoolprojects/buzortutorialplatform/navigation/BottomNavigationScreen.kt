/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package awesomenessstudios.schoolprojects.buzortutorialplatform.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.MarkChatUnread
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationScreen(
    val title: String = "",
    val selectedItem: Unit,
    val unSelectedItem: Unit,
    val notificationCount: Int? = null,
    val route: String = ""
)

sealed class StudentBottomBarScreen(
    val title: String,
    val icon: ImageVector,
    val route: String
){
    object Transactions : StudentBottomBarScreen("Transaction", Icons.Default.History, "student_transactions",)
    object PayFees : StudentBottomBarScreen("Pay Fees", Icons.Default.Payments, "student_pay_fees",)
    object TakeLoan : StudentBottomBarScreen("Take Loan", Icons.Default.VolunteerActivism, "student_take_loan",)
    object PayDues : StudentBottomBarScreen("Pay Dues", Icons.Default.Gavel, "student_pay_dues",)
    object Savings : StudentBottomBarScreen("Savings", Icons.Default.Savings, "student_savings",)

}

sealed class TeacherBottomBarScreen(
    val title: String,
    val icon: ImageVector,
    val route: String
){
    object Sessions : TeacherBottomBarScreen("Accounts", Icons.Default.VideoCall, "teacher_sessions",)
    object Courses : TeacherBottomBarScreen("Fees", Icons.Default.MenuBook, "teacher_courses",)
    object Payments : TeacherBottomBarScreen("Loans", Icons.Default.Payments, "teacher_payments",)
    object Profile : TeacherBottomBarScreen("Dues", Icons.Default.Person, "teacher_profile",)

}
