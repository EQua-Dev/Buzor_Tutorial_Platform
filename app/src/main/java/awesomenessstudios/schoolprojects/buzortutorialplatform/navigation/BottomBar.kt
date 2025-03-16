package awesomenessstudios.schoolprojects.buzortutorialplatform.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState


@Composable
fun StudentBottomBar(navController: NavHostController) {
    val screens = listOf(
        StudentBottomBarScreen.Sessions,
        StudentBottomBarScreen.Courses,
        StudentBottomBarScreen.Wallet,
        StudentBottomBarScreen.Profile,
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        screens.forEach { screen ->
            StudentAddItem(
                screen = screen,
                currentDestination = currentDestination,
                navController = navController
            )

        }
    }

}


@Composable
fun RowScope.StudentAddItem(
    screen: StudentBottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    NavigationBarItem(
        selected = currentDestination?.hierarchy?.any {
            it.route == screen.route
        } == true,
        label = {
            Text(text = screen.title)
        },
        onClick = { navController.navigate(screen.route) },
        icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) })
}


@Composable
fun TeacherBottomBar(navController: NavHostController) {
    val screens = listOf(
        TeacherBottomBarScreen.Sessions,
        TeacherBottomBarScreen.Courses,
        TeacherBottomBarScreen.Payments,
        TeacherBottomBarScreen.Profile,
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        screens.forEach { screen ->
            TeacherAddItem(
                screen = screen,
                currentDestination = currentDestination,
                navController = navController
            )

        }
    }

}


@Composable
fun RowScope.TeacherAddItem(
    screen: TeacherBottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    NavigationBarItem(
        selected = currentDestination?.hierarchy?.any {
            it.route == screen.route
        } == true,
        label = {
            Text(text = screen.title)
        },
        onClick = { navController.navigate(screen.route) },
        icon = { Icon(imageVector = screen.icon, contentDescription = screen.title) })
}