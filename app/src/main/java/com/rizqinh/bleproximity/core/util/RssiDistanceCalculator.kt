package com.rizqinh.bleproximity.core.util

import com.rizqinh.bleproximity.domain.model.SignalCategory
import kotlin.math.pow

/**
 * Utility helper providing distance calculation from RSSI values using the
 * standard Log-Distance Path Loss model, and classification into signal zones.
 */
object RssiDistanceCalculator {

    /**
     * Default reference RSSI at 1 meter distance (TxPower / Measured Power).
     * Typically ranges between -59 dBm to -65 dBm for BLE devices.
     */
    const val DEFAULT_MEASURED_POWER = -59

    /**
     * Environmental Path Loss Exponent (N).
     * - 2.0: Free space
     * - 2.0 to 3.0: Indoor open space
     * - 3.0 to 4.0: Indoor with obstacles / walls
     */
    const val DEFAULT_PATH_LOSS_EXPONENT = 2.5

    /**
     * Calculates estimated distance in meters from RSSI value.
     * Formula: Distance = 10 ^ ((Measured Power - RSSI) / (10 * N))
     *
     * @param rssi Current received signal strength in dBm.
     * @param txPower Reference RSSI at 1 meter (default -59 dBm).
     * @param pathLossExponent Environmental constant (default 2.5).
     * @return Calculated distance in meters rounded to 2 decimal places.
     */
    fun calculateDistance(
        rssi: Int,
        txPower: Int = DEFAULT_MEASURED_POWER,
        pathLossExponent: Double = DEFAULT_PATH_LOSS_EXPONENT,
    ): Double {
        if (rssi == 0) return -1.0 // Invalid RSSI
        val exponent = (txPower - rssi) / (10.0 * pathLossExponent)
        val rawDistance = 10.0.pow(exponent)
        return kotlin.math.round(rawDistance * 100.0) / 100.0
    }

    /**
     * Categorizes RSSI signal strength into one of 6 semantic proximity zones.
     *
     * @param rssi RSSI value in dBm.
     * @param isTimedOut If true, automatically forces classification to [SignalCategory.LOST].
     * @return Corresponding [SignalCategory].
     */
    fun categorizeRssi(rssi: Int, isTimedOut: Boolean = false): SignalCategory {
        if (isTimedOut) return SignalCategory.LOST

        return when (rssi) {
            in -30..0 -> SignalCategory.VERY_STRONG
            in -50 until -30 -> SignalCategory.STRONG
            in -70 until -50 -> SignalCategory.FAIR
            in -80 until -70 -> SignalCategory.WEAK
            in -90 until -80 -> SignalCategory.VERY_WEAK
            else -> SignalCategory.LOST // < -90 dBm
        }
    }
}
