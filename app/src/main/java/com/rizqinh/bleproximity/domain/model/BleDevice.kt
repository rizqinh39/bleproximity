package com.rizqinh.bleproximity.domain.model

/**
 * Domain model representing a detected Bluetooth Low Energy (BLE) peripheral.
 *
 * @property macAddress Hardware address of the device (unique identifier).
 * @property name Broadcast device name or fallback string ("Unknown BLE Device").
 * @property rssi Current Received Signal Strength Indicator in dBm.
 * @property estimatedDistanceMeters Estimated distance calculated via Log-Distance Path Loss model.
 * @property category Proximity zone classification from [SignalCategory].
 * @property lastSeenTimestamp Unix epoch timestamp (ms) when last packet was captured.
 * @property packetCount Total number of advertising packets received during active session.
 * @property minRssi Minimum RSSI observed in active tracking session.
 * @property maxRssi Maximum RSSI observed in active tracking session.
 * @property avgRssi Rolling average RSSI observed in active tracking session.
 */
data class BleDevice(
    val macAddress: String,
    val name: String = "Unknown BLE Device",
    val rssi: Int,
    val estimatedDistanceMeters: Double,
    val category: SignalCategory,
    val lastSeenTimestamp: Long = System.currentTimeMillis(),
    val packetCount: Int = 1,
    val minRssi: Int = rssi,
    val maxRssi: Int = rssi,
    val avgRssi: Double = rssi.toDouble(),
)
