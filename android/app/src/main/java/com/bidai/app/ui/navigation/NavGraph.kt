package com.bidai.app.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bidai.app.ui.screens.auth.AuthViewModel
import com.bidai.app.ui.screens.auth.LoginScreen
import com.bidai.app.ui.screens.home.HomeScreen
import com.bidai.app.ui.screens.home.HomeViewModel
import com.bidai.app.ui.screens.sell.OneClickSellScreen
import com.bidai.app.ui.screens.sell.SellViewModel
import com.bidai.app.utils.TokenManager

object ScreenRoutes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val SELL = "sell"
}

@Composable
fun BidAiNavGraph(
    navController: NavHostController,
    context: Context
) {
    val tokenManager = TokenManager.getInstance(context)
    val startDestination = if (tokenManager.isLoggedIn()) ScreenRoutes.HOME else ScreenRoutes.LOGIN

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(ScreenRoutes.LOGIN) {
            val authViewModel = AuthViewModel(context)
            LoginScreen(
                viewModel = authViewModel,
                onAuthSuccess = {
                    navController.navigate(ScreenRoutes.HOME) {
                        popUpTo(ScreenRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(ScreenRoutes.HOME) {
            val homeViewModel = HomeViewModel(context)
            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToSell = {
                    navController.navigate(ScreenRoutes.SELL)
                },
                onLogout = {
                    tokenManager.clearAuth()
                    navController.navigate(ScreenRoutes.LOGIN) {
                        popUpTo(ScreenRoutes.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(ScreenRoutes.SELL) {
            val sellViewModel = SellViewModel(context)
            OneClickSellScreen(
                viewModel = sellViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onListingSuccess = {
                    navController.navigate(ScreenRoutes.HOME) {
                        popUpTo(ScreenRoutes.HOME) { inclusive = true }
                    }
                }
            )
        }
    }
}
