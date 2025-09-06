package com.alexmls.echojournal.app.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.alexmls.echojournal.echo.presentation.create_echo.CreateEchoRoot
import com.alexmls.echojournal.echo.presentation.echo.HomeScreen
import com.alexmls.echojournal.echo.presentation.settings.SettingsRoot
import com.alexmls.echojournal.echo.presentation.util.toCreateEchoRoute


@Composable
fun Navigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Home
    ){
        composable<NavigationRoute.Home> {
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