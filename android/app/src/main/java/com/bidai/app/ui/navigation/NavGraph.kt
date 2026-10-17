package com.bidai.app.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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

@Suppress("UNCHECKED_CAST")
class AppViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val appContext = context.applicationContext
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(appContext) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(appContext) as T
            modelClass.isAssignableFrom(SellViewModel::class.java) -> SellViewModel(appContext) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

@Composable
fun BidAiNavGraph(
    navController: NavHostController,
    context: Context
) {
    val tokenManager = TokenManager.getInstance(context)
    val startDestination = if (tokenManager.isLoggedIn()) ScreenRoutes.HOME else ScreenRoutes.LOGIN
    val factory = AppViewModelFactory(context)

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(ScreenRoutes.LOGIN) {
            val authViewModel: AuthViewModel = viewModel(factory = factory)
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
            val homeViewModel: HomeViewModel = viewModel(factory = factory)
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
            val sellViewModel: SellViewModel = viewModel(factory = factory)
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

