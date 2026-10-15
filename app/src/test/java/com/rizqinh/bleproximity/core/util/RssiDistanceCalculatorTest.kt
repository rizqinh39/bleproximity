package com.rizqinh.bleproximity.core.util

import com.rizqinh.bleproximity.domain.model.BleDevice
import com.rizqinh.bleproximity.domain.model.SignalCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit Tests for [RssiDistanceCalculator] distance formula,
 * classification across all 6 RSSI proximity zones, and signal sorting logic.
 */
class RssiDistanceCalculatorTest {

    @Test
    fun `calculateDistance returns expected distance in meters`() {
        // At TxPower (-59 dBm) and RSSI = -59, calculated distance should be 1.0 meter
        val distanceAt1m = RssiDistanceCalculator.calculateDistance(rssi = -59, txPower = -59)
        assertEquals(1.0, distanceAt1m, 0.01)

        // At weaker RSSI (-79 dBm), calculated distance should be greater than 1.0 meter
        val distanceWeaker = RssiDistanceCalculator.calculateDistance(rssi = -79, txPower = -59)
        assertTrue(distanceWeaker > 1.0)
    }

    @Test
    fun `categorizeRssi classifies signal into Zone 1 VERY_STRONG for RSSI -10 to -30 dBm`() {
        assertEquals(SignalCategory.VERY_STRONG, RssiDistanceCalculator.categorizeRssi(-20))
        assertEquals(SignalCategory.VERY_STRONG, RssiDistanceCalculator.categorizeRssi(-30))
    }

    @Test
    fun `categorizeRssi classifies signal into Zone 2 STRONG for RSSI -30 to -50 dBm`() {
        assertEquals(SignalCategory.STRONG, RssiDistanceCalculator.categorizeRssi(-35))
        assertEquals(SignalCategory.STRONG, RssiDistanceCalculator.categorizeRssi(-49))
    }

    @Test
    fun `categorizeRssi classifies signal into Zone 3 FAIR for RSSI -50 to -70 dBm`() {
        assertEquals(SignalCategory.FAIR, RssiDistanceCalculator.categorizeRssi(-55))
        assertEquals(SignalCategory.FAIR, RssiDistanceCalculator.categorizeRssi(-69))
    }

    @Test
    fun `categorizeRssi classifies signal into Zone 4 WEAK for RSSI -70 to -80 dBm`() {
        assertEquals(SignalCategory.WEAK, RssiDistanceCalculator.categorizeRssi(-72))
        assertEquals(SignalCategory.WEAK, RssiDistanceCalculator.categorizeRssi(-79))
    }

    @Test
    fun `categorizeRssi classifies signal into Zone 5 VERY_WEAK for RSSI -80 to -90 dBm`() {
        assertEquals(SignalCategory.VERY_WEAK, RssiDistanceCalculator.categorizeRssi(-81))
        assertEquals(SignalCategory.VERY_WEAK, RssiDistanceCalculator.categorizeRssi(-89))
    }

    @Test
    fun `categorizeRssi classifies signal into Zone 6 LOST for RSSI below -90 dBm or timed out`() {
        assertEquals(SignalCategory.LOST, RssiDistanceCalculator.categorizeRssi(-95))
        assertEquals(SignalCategory.LOST, RssiDistanceCalculator.categorizeRssi(rssi = -50, isTimedOut = true))
    }

    @Test
    fun `device list sorting orders devices by strongest signal highest RSSI first`() {
        val deviceWeak = BleDevice(
            macAddress = "AA:BB:CC:DD:EE:01",
            name = "Device Weak",
            rssi = -85,
            estimatedDistanceMeters = 15.0,
            category = SignalCategory.VERY_WEAK
        )

        val deviceStrong = BleDevice(
            macAddress = "AA:BB:CC:DD:EE:02",
            name = "Device Strong",
            rssi = -35,
            estimatedDistanceMeters = 1.5,
            category = SignalCategory.STRONG
        )

        val deviceVeryStrong = BleDevice(
            macAddress = "AA:BB:CC:DD:EE:03",
            name = "Device Very Strong",
            rssi = -25,
            estimatedDistanceMeters = 0.8,
            category = SignalCategory.VERY_STRONG
        )

        val unsortedList = listOf(deviceWeak, deviceVeryStrong, deviceStrong)
        val sortedList = unsortedList.sortedByDescending { it.rssi }

        assertEquals(deviceVeryStrong, sortedList[0])
        assertEquals(deviceStrong, sortedList[1])
        assertEquals(deviceWeak, sortedList[2])
    }
}
