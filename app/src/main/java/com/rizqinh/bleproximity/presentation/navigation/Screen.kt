package com.rizqinh.bleproximity.presentation.navigation

/**
 * Sealed class defining type-safe Navigation routes for the application screens.
 */
sealed class Screen(val route: String) {
    object Scanner : Screen("scanner")
    object History : Screen("history")
    object RadarDetail : Screen("radar_detail/{macAddress}") {
        fun createRoute(macAddress: String): String = "radar_detail/$macAddress"
    }
}
