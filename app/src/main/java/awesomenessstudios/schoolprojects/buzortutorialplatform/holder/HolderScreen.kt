package awesomenessstudios.schoolprojects.buzortutorialplatform.holder

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import awesomenessstudios.schoolprojects.buzortutorialplatform.data.enums.UserRole
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.StudentHomeScreen
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.student.auth.presentation.signup.StudentRegistrationScreen
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.TeacherHomeScreen
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.auth.presentation.forgotpassword.ForgotPasswordScreen
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.auth.presentation.login.LoginScreen
import awesomenessstudios.schoolprojects.buzortutorialplatform.features.teacher.auth.presentation.signup.TeacherRegistrationScreen
import awesomenessstudios.schoolprojects.buzortutorialplatform.navigation.Screen
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.Common.mAuth
import awesomenessstudios.schoolprojects.buzortutorialplatform.utils.getDp
import com.awesomenessstudios.schoolprojects.criticalthinkingappforkids.providers.LocalNavHost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HolderScreen(
    onStatusBarColorChange: (color: Color) -> Unit,
    holderViewModel: HolderViewModel = hiltViewModel(),
) {

    val TAG = "HolderScreen"
    /*  val destinations = remember {
          listOf(Screen.Home, Screen.Notifications, Screen.Bookmark, Screen.Profile)
      }*/

    /** Our navigation controller that the MainActivity provides */
    val controller = LocalNavHost.current

    /** The current active navigation route */
    val currentRouteAsState = getActiveRoute(navController = controller)

    /** The current logged user, which is null by default */

    /** The main app's scaffold state */
    val scaffoldState = rememberBottomSheetScaffoldState()

    /** The coroutine scope */
    val scope = rememberCoroutineScope()

    /** Dynamic snack bar color */
    val (snackBarColor, setSnackBarColor) = remember {
        mutableStateOf(Color.White)
    }

    /** SnackBar appear/disappear transition */
    val snackBarTransition = updateTransition(
        targetState = scaffoldState.snackbarHostState,
        label = "SnackBarTransition"
    )

    /** SnackBar animated offset */
    val snackBarOffsetAnim by snackBarTransition.animateDp(
        label = "snackBarOffsetAnim",
        transitionSpec = {
            TweenSpec(
                durationMillis = 300,
                easing = LinearEasing,
            )
        }
    ) {
        when (it.currentSnackbarData) {
            null -> {
                100.getDp()
            }

            else -> {
                0.getDp()
            }
        }
    }

    Box {
        /** Cart offset on the screen */
        val (cartOffset, setCartOffset) = remember {
            mutableStateOf(IntOffset(0, 0))
        }
        ScaffoldSection(
            controller = controller,
            scaffoldState = scaffoldState,
            onStatusBarColorChange = onStatusBarColorChange,
            onNavigationRequested = { route, removePreviousRoute ->
                if (removePreviousRoute) {
                    controller.popBackStack()
                }
                controller.navigate(route)
            },
            onBackRequested = {
                controller.popBackStack()
            },
            onAuthenticated = { userType ->
                var navRoute = ""
                when (userType) {
                    /*Common.UserTypes.STUDENT.userType -> navRoute = Screen.StudentLanding.route
                    Common.UserTypes.LECTURER.userType -> navRoute =
                        Screen.LecturerLandingScreen.route*/
                }
                controller.navigate(navRoute) {
                    /* popUpTo(Screen.Login.route) {
                         inclusive = true
                     }*/
                }
            },
            onAccountCreated = {
                //nav to register courses
                /*controller.navigate(Screen.CourseRegistration.route) {
                    popUpTo(Screen.Signup.route) {
                        inclusive = true
                    }
                }*/
            },
            onNewScreenRequest = { route, patientId ->
                controller.navigate(route.replace("{patientId}", "$patientId"))
            },
            onLogoutRequested = {
                mAuth.signOut()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldSection(
    controller: NavHostController,
    scaffoldState: BottomSheetScaffoldState,
    onStatusBarColorChange: (color: Color) -> Unit,
    onNavigationRequested: (route: String, removePreviousRoute: Boolean) -> Unit,
    onBackRequested: () -> Unit,
    onAuthenticated: (userType: String) -> Unit,
    onAccountCreated: () -> Unit,
    onNewScreenRequest: (route: String, id: String?) -> Unit,
    onLogoutRequested: () -> Unit
) {
    Scaffold(
        //scaffoldState = scaffoldState,
        snackbarHost = {
            scaffoldState.snackbarHostState
        },
    ) { paddingValues ->
        Column(
            Modifier.padding(paddingValues)
        ) {
            NavHost(
                modifier = Modifier.weight(1f),
                navController = controller,
                startDestination = Screen.InitRoleTypeScreen.route
            ) {
                composable(Screen.InitRoleTypeScreen.route) {
                    onStatusBarColorChange(MaterialTheme.colorScheme.background)
                    InitScreen(
                        onRoleSelected = { _ ->
                            controller.navigate(Screen.Login.route)
                        },
                    )
                }
                composable(Screen.TeacherRegistration.route) {
                    onStatusBarColorChange(MaterialTheme.colorScheme.background)
                    TeacherRegistrationScreen(
                        onRegistrationSuccess = { controller.navigate(Screen.Login.route) },
                    )
                }
                composable(Screen.Login.route) {
                    onStatusBarColorChange(MaterialTheme.colorScheme.background)
                    LoginScreen(
                        onLoginSuccess = { userRole ->
                            if (userRole === UserRole.TEACHER.name) {
                                controller.navigate(Screen.TeacherHome.route)
                            } else {
                                controller.navigate(Screen.StudentHome.route)
                            }
                        },
                        onForgotPasswordClicked = { controller.navigate(Screen.ForgotPassword.route) },
                        onRegisterClicked = {
                                userRole ->
                            if (userRole === UserRole.TEACHER.name) {
                                controller.navigate(Screen.TeacherRegistration.route)
                            } else {
                                controller.navigate(Screen.StudentRegistration.route)
                            }
                        }
                    )
                }
                composable(Screen.ForgotPassword.route) {
                    onStatusBarColorChange(MaterialTheme.colorScheme.background)
                    ForgotPasswordScreen(
                        onPasswordResetSuccess = { controller.navigate(Screen.Login.route) }
                    )
                }
                composable(Screen.TeacherHome.route) {
                    onStatusBarColorChange(MaterialTheme.colorScheme.background)
                    TeacherHomeScreen(
                        baseNavHostController = controller,
                        onNavigationRequested = onNavigationRequested,
                    )
                }
                composable(Screen.StudentRegistration.route) {
                    onStatusBarColorChange(MaterialTheme.colorScheme.background)
                    StudentRegistrationScreen(
                        onRegistrationSuccess = { controller.navigate(Screen.Login.route) },
                    )
                }
                composable(Screen.StudentHome.route) {
                    onStatusBarColorChange(MaterialTheme.colorScheme.background)
                    StudentHomeScreen(
                        baseNavHostController = controller,
                        onNavigationRequested = onNavigationRequested,
                    )
                }
                /*composable(
                    Screen.ChildHome.route,
                    arguments = listOf(
                        navArgument(name = "childId") { type = NavType.StringType }
                    ),
                ) {
                    onStatusBarColorChange(MaterialTheme.colorScheme.background)
                    val childId = it.arguments?.getString("childId")

                    ChildHomeScreen(
                        childId = childId!!,
                        navController = controller,
                        onNavigationRequested = onNavigationRequested,
                        onCategorySelected = onCategorySelected,
                        //onLeaderboardSelected = onLeaderBoardSelected
                    )
                }*/

            }
        }
    }
}

/**
 * A function that is used to get the active route in our Navigation Graph , should return the splash route if it's null
 */
@Composable
fun getActiveRoute(navController: NavHostController): String {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route ?: "splash"
}
