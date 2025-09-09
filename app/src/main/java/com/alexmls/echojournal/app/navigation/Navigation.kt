package com.alexmls.echojournal.app.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.alexmls.echojournal.echo.presentation.create_echo.CreateEchoRoot
import com.alexmls.echojournal.echo.presentation.echo.HomeScreen
import com.alexmls.echojournal.echo.presentation.settings.SettingsRoot
import com.alexmls.echojournal.echo.presentation.util.toCreateEchoRoute

const val ACTION_CREATE_ECHO = "com.alexmls.CREATE_ECHO"

@Composable
fun Navigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Home(
            startRecording = false
        )
    ){
        composable<NavigationRoute.Home>(
            deepLinks = listOf(
                navDeepLink<NavigationRoute.Home>(
                    basePath = "https://echojournal.com/echo"
                ){
                    action = ACTION_CREATE_ECHO
                }
            )
        ){
            HomeScreen(
                onNavigateToCreateEcho = { details ->
                    navController.navigate(details.toCreateEchoRoute())
                },
                onNavigateToSettings = {
                    navController.navigate(NavigationRoute.Settings)
                }
            )
        }
        composable<NavigationRoute.CreateEcho> {
           CreateEchoRoot(onConfirmLeave = navController::navigateUp)
        }
        composable<NavigationRoute.Settings> {
            SettingsRoot(
                onGoBack = navController::navigateUp
            )
        }
    }
}