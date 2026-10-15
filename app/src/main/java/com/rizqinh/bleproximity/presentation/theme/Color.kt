package com.rizqinh.bleproximity.presentation.theme

import androidx.compose.ui.graphics.Color

// Design System Base Colors
val SurfaceBackground = Color(0xFFF8FAFC)
val CardSurface = Color(0xFFFFFFFF)
val PrimaryBlue = Color(0xFF2563EB)
val PrimaryBlueVariant = Color(0xFF1D4ED8)
val DangerRed = Color(0xFFDC2626)

// Text & Content Colors
val TextPrimary = Color(0xFF0F172A)
val TextSecondary = Color(0xFF64748B)
val BorderLight = Color(0xFFE2E8F0)

// 6 Proximity Signal Zone Colors
val ZoneVeryStrongEmerald = Color(0xFF10B981) // -10 to -30 dBm
val ZoneStrongLime = Color(0xFF84CC16)       // -30 to -50 dBm
val ZoneFairAmber = Color(0xFFEAB308)        // -50 to -70 dBm
val ZoneWeakOrange = Color(0xFFF97316)       // -70 to -80 dBm
val ZoneVeryWeakRed = Color(0xFFEF4444)      // -80 to -90 dBm
val ZoneLostGray = Color(0xFF94A3B8)         // < -90 dBm / Timeout
