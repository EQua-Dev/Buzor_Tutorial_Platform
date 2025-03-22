package awesomenessstudios.schoolprojects.buzortutorialplatform.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import awesomenessstudios.schoolprojects.buzortutorialplatform.R

sealed class Screen(
    val route: String,
    @StringRes val title: Int? = null,
    @DrawableRes val icon: Int? = null,
) {
    object Splash : Screen(
        route = "splash",
    )

    object InitRoleTypeScreen : Screen(
        route = "role_type",
    )

    object StudentRegistration : Screen(
        route = "student_registration",
        title = R.string.student_registration,
    )

    object TeacherRegistration : Screen(
        route = "teacher_registration",
        title = R.string.teacher_registration,
    )

    object Login : Screen(
        route = "login",
        title = R.string.login,
    )

    object ForgotPassword : Screen(
        route = "forgot_password",
        title = R.string.forgot_password,
    )

    object TeacherHome : Screen(
        route = "teacher_home",
        title = R.string.teacher_home,
    )
    object StudentHome : Screen(
        route = "student_home",
        title = R.string.student_home,
    )
    object CreateWalletScreen : Screen(
        route = "create_wallet_screen",
        title = R.string.create_wallet,
    )
    object CreateCourseFlowScreen : Screen(
        route = "create_course_flow_screen",
        title = R.string.create_create,
    )
    /*
        object ChildHome : Screen(
            route = "childhome/{childId}",
            title = R.string.child_home,
        )

        object CategoryOverview : Screen(
            route = "categoryoverview/{childId}/{category}/{childStage}",
            title = R.string.category_overview,
        )

        object ActivityTypeOverview : Screen(
            route = "activitytypeoverview/{activityTypeKey}/{childId}/{category}/{childStage}",
            title = R.string.activity_type_overview,
        )

        object ActivityTypeRule : Screen(
            route = "activitytyperule/{childId}/{categoryKey}/{childStage}/{activityTypeKey}/{selectedDifficulty}/{lastScore}/{lastPlayed}}",
            title = R.string.activity_type_rule,
        )

        object Quiz : Screen(
            route = "quiz/{childId}/{categoryKey}/{childStage}/{difficultyLevel}",
            title = R.string.quiz,
        )

        object Game : Screen(
            route = "game/{childId}/{categoryKey}/{difficultyLevel}/{childStage}",
            title = R.string.game,
        )

        object Video : Screen(
            route = "video/{childStage}/{category}",
            title = R.string.video,
        )

        object OutdoorTasks : Screen(
            route = "outdoortasks/{childStage}/{difficulty}/{category}",
            title = R.string.outdoor_tasks,
        )

        object QuizResult : Screen(
            route = "quizresult/{score}/{childId}/{category}/{difficulty}/{childStage}/{quizDoneReason}",
            title = R.string.quiz_result,
        )
        object Leaderboard : Screen(
            route = "leaderboard/{childId}/{category}/{childStage}/{difficulty}",
            title = R.string.leaderboard,
        )*/
}




