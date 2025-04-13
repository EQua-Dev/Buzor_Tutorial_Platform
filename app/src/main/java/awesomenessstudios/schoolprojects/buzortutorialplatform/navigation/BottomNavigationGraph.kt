/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package awesomenessstudios.schoolprojects.buzortutorialplatform.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.courses.coursedetail.presentation.StudentCourseDetailScreen
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.courses.coursedetail.presentation.ViewContentScreen
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.courses.presentation.StudentCoursesScreen
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.profile.presentation.StudentProfileScreen
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.sessions.presentation.StudentSessionScreen
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.wallet.presentation.StudentWalletScreen
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.courses.createcourse.CreateCourseFlowScreen
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.courses.presentation.TeacherCoursesScreen
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.payments.presentation.TeacherPaymentsScreen
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.profile.presentation.TeacherProfileScreen
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.sessions.presentation.TeacherSessionScreen


@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun StudentBottomNavigationGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = StudentBottomBarScreen.Sessions.route
    ) {
        composable(
            route = StudentBottomBarScreen.Sessions.route
        ) {
            StudentSessionScreen(navController = navController)
        }
        composable(
            route = StudentBottomBarScreen.Courses.route
        ) {
            StudentCoursesScreen(navController = navController)
        }
        composable(
            route = StudentBottomBarScreen.Wallet.route
        ) {
            StudentWalletScreen(navController = navController)
        }
        composable(
            route = StudentBottomBarScreen.Profile.route
        ) {
            StudentProfileScreen(navController = navController)
        }

        composable(
            Screen.StudentCourseDetailScreen.route,
            arguments = listOf(
                navArgument(name = "courseId") { type = NavType.StringType }
            ),
        ) {
            val courseId = it.arguments?.getString("courseId")
            StudentCourseDetailScreen(navController = navController, courseId = courseId!!)
        }
        composable(
            Screen.CourseContentViewerScreen.route,
            arguments = listOf(
                navArgument(name = "url") { type = NavType.StringType }
            ),
        ) {
            val url = it.arguments?.getString("url")
            ViewContentScreen(navController = navController, url = url!!)
        }
        /*composable(
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

@Composable
fun TeacherBottomNavigationGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = TeacherBottomBarScreen.Sessions.route
    ) {
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
        composable(
            route = Screen.CreateCourseFlowScreen.route
        ) {
            CreateCourseFlowScreen(onCourseCreated = {
                navController.popBackStack()
            })
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