/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package awesomenessstudios.schoolprojects.buzortutorialplatform.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.courses.presentation.TeacherCoursesScreen
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.payments.presentation.TeacherPaymentsScreen
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.profile.presentation.TeacherProfileScreen
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.sessions.presentation.TeacherSessionScreen

/*
@Composable
fun StudentBottomNavigationGraph(navController: NavHostController) {

    NavHost(navController = navController, startDestination = BottomBarScreen.Transactions.route) {
        composable(
            route = BottomBarScreen.Transactions.route
        ) {
            StudentTransactions(navController = navController)
        }
        composable(
            route = BottomBarScreen.PayFees.route
        ) {
            StudentPayFees(navController = navController)
        }
        composable(
            route = BottomBarScreen.TakeLoan.route
        ) {
            StudentTakeLoan(navController = navController)
        }
        composable(
            route = BottomBarScreen.PayDues.route
        ) {
            StudentPayDues(navController = navController)
        }
        composable(
            route = BottomBarScreen.Savings.route
        ) {
            StudentSavings(navController = navController)
        }
        composable(
            Screen.FeesSemester.route,
            arguments = listOf(
                navArgument(name = "level") { type = NavType.StringType },
                navArgument(name = "semester") { type = NavType.StringType }
            ),
        ) {
            val level = it.arguments?.getString("level")
            val semester = it.arguments?.getString("semester")
            FeesPaymentScreen(
                level = level!!, semester = semester!!,
                onBack = { navController.popBackStack() },
                //onBackRequested = onBackRequested,

            )
        }
        composable(
            Screen.DuesSemesterScreen.route,
            arguments = listOf(
                navArgument(name = "level") { type = NavType.StringType },
                navArgument(name = "semester") { type = NavType.StringType }
            ),
        ) {
            val level = it.arguments?.getString("level")
            val semester = it.arguments?.getString("semester")
            DuesPaymentScreen(
                level = level!!, semester = semester!!,
                onBack = { navController.popBackStack() },
                onPay = {}
                //onBackRequested = onBackRequested,

            )
        }
    }

}*/

@Composable
fun TeacherBottomNavigationGraph(navController: NavHostController) {

    NavHost(navController = navController, startDestination = TeacherBottomBarScreen.Sessions.route) {
        composable(
            route = TeacherBottomBarScreen.Sessions.route
        ) {
            TeacherSessionScreen(navController = navController)
        }
        composable(
            route = TeacherBottomBarScreen.Courses.route
        ) {
            TeacherCoursesScreen(navController = navController)
        }
        composable(
            route = TeacherBottomBarScreen.Payments.route
        ) {
            TeacherPaymentsScreen(navController = navController)
        }
        composable(
            route = TeacherBottomBarScreen.Profile.route
        ) {
            TeacherProfileScreen(navController = navController)
        }
        /*
        composable(
            Screen.FeesSemester.route,
            arguments = listOf(
                navArgument(name = "level") { type = NavType.StringType },
                navArgument(name = "semester") { type = NavType.StringType }
            ),
        ) {
            val level = it.arguments?.getString("level")
            val semester = it.arguments?.getString("semester")
            FeesPaymentScreen(
                level = level!!, semester = semester!!,
                onBack = { navController.popBackStack() },
                //onBackRequested = onBackRequested,

            )
        }
        composable(
            Screen.DuesSemesterScreen.route,
            arguments = listOf(
                navArgument(name = "level") { type = NavType.StringType },
                navArgument(name = "semester") { type = NavType.StringType }
            ),
        ) {
            val level = it.arguments?.getString("level")
            val semester = it.arguments?.getString("semester")
            DuesPaymentScreen(
                level = level!!, semester = semester!!,
                onBack = { navController.popBackStack() },
                onPay = {}
                //onBackRequested = onBackRequested,

            )
        }*/
    }

}