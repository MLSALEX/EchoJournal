package com.alexmls.echojournal.app.navigation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.alexmls.echojournal.echo.presentation.echo.HomeScreen
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
                }
            )
        }
        composable<NavigationRoute.CreateEcho> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Text("Create Echo Screen")
            }
        }
    }
}