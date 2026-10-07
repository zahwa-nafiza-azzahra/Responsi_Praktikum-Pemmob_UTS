package com.example.responsipraktikum.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.responsipraktikum.ui.screen.DetailScreen
import com.example.responsipraktikum.ui.screen.HomeScreen
import com.example.responsipraktikum.ui.viewmodel.DetailViewModel
import com.example.responsipraktikum.ui.viewmodel.HomeViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory()),
    detailViewModel: DetailViewModel = viewModel(factory = DetailViewModel.Factory())
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = homeViewModel,
                onGameClick = { gameId ->
                    navController.navigate(Screen.Detail.createRoute(gameId))
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("gameId") { type = NavType.IntType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getInt("gameId") ?: 0
            DetailScreen(
                gameId = gameId,
                viewModel = detailViewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
