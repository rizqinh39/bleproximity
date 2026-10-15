package com.rizqinh.bleproximity.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rizqinh.bleproximity.presentation.history.HistoryScreen
import com.rizqinh.bleproximity.presentation.history.HistoryViewModel
import com.rizqinh.bleproximity.presentation.radar.RadarDetailScreen
import com.rizqinh.bleproximity.presentation.scanner.ScannerScreen
import com.rizqinh.bleproximity.presentation.scanner.ScannerViewModel

/**
 * Navigation Graph managing transitions between Scanner Dashboard, Radar Detail, and History Screen.
 */
@Composable
fun BleNavGraph(
    navController: NavHostController,
    scannerViewModel: ScannerViewModel,
    onRequestPermissions: () -> Unit,
    onEnableBluetooth: () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Scanner.route,
    ) {
        // Screen 1: Dashboard Utama (Scanner)
        composable(route = Screen.Scanner.route) {
            ScannerScreen(
                viewModel = scannerViewModel,
                onNavigateToRadar = { device ->
                    navController.navigate(Screen.RadarDetail.createRoute(device.macAddress))
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onRequestPermissions = onRequestPermissions,
                onEnableBluetooth = onEnableBluetooth,
            )
        }

        // Screen 2: Detail Pelacakan (Radar View)
        composable(
            route = Screen.RadarDetail.route,
            arguments = listOf(
                navArgument("macAddress") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val macAddress = backStackEntry.arguments?.getString("macAddress") ?: ""
            RadarDetailScreen(
                macAddress = macAddress,
                viewModel = scannerViewModel,
                onNavigateBack = { navController.popBackStack() },
            )
        }

        // Screen 3: Riwayat Perangkat (History Log)
        composable(route = Screen.History.route) {
            val historyViewModel: HistoryViewModel = hiltViewModel()
            HistoryScreen(
                viewModel = historyViewModel,
                onNavigateBack = { navController.popBackStack() },
                onSelectDevice = { device ->
                    navController.navigate(Screen.RadarDetail.createRoute(device.macAddress))
                },
            )
        }
    }
}
