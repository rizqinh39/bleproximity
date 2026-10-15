package com.rizqinh.bleproximity.domain.model

import androidx.compose.ui.graphics.Color

/**
 * Enum representing the 6 exact Signal Categories / Proximity Zones based on RSSI values.
 *
 * Each zone defines:
 * - [label]: Indonesian description of the signal strength.
 * - [estimatedDistanceRange]: Approximate distance in meters for live display.
 * - [colorHex]: Hexadecimal color code matching the design system specifications.
 * - [color]: Jetpack Compose Color representation for UI rendering.
 */
enum class SignalCategory(
    val label: String,
    val estimatedDistanceRange: String,
    val colorHex: Long,
) {
    // Zone 1: -10 to -30 dBm
    VERY_STRONG(
        label = "Sangat Kuat (Sangat Dekat)",
        estimatedDistanceRange = "< 1 meter",
        colorHex = 0xFF10B981 // Emerald
    ),

    // Zone 2: -30 to -50 dBm
    STRONG(
        label = "Kuat (Dekat)",
        estimatedDistanceRange = "1-3 meter",
        colorHex = 0xFF84CC16 // Lime
    ),

    // Zone 3: -50 to -70 dBm
    FAIR(
        label = "Cukup / Baik",
        estimatedDistanceRange = "3-10 meter",
        colorHex = 0xFFEAB308 // Amber
    ),

    // Zone 4: -70 to -80 dBm
    WEAK(
        label = "Lemah",
        estimatedDistanceRange = "10-20 meter",
        colorHex = 0xFFF97316 // Orange
    ),

    // Zone 5: -80 to -90 dBm
    VERY_WEAK(
        label = "Sangat Lemah / Putus-putus",
        estimatedDistanceRange = "> 20 meter (Batas jangkauan)",
        colorHex = 0xFFEF4444 // Red
    ),

    // Zone 6: < -90 dBm or Timeout > 5s
    LOST(
        label = "Sinyal Hilang (Lost)",
        estimatedDistanceRange = "Terputus / Di luar jangkauan",
        colorHex = 0xFF94A3B8 // Gray
    );

    /**
     * Helper property returning Compose [Color] directly from hex token.
     */
    val color: Color
        get() = Color(colorHex)
}
