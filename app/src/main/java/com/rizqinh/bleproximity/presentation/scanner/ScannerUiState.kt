package com.rizqinh.bleproximity.presentation.scanner

import com.rizqinh.bleproximity.domain.model.BleDevice

/**
 * Immutable State class representing the complete UI state for the Scanner Dashboard screen.
 *
 * @property devices Filtered and sorted list of BLE devices displayed in LazyColumn.
 * @property rawDevices Unfiltered raw list of discovered BLE devices.
 * @property isScanning True if active BLE scanning is running.
 * @property isBluetoothEnabled True if the device Bluetooth adapter is switched ON.
 * @property hasPermissions True if all required BLE runtime permissions are granted.
 * @property searchQuery Current text query entered in the search bar.
 * @property minRssiThreshold Minimum RSSI filter threshold in dBm (e.g. -100 dBm to -30 dBm).
 * @property isLoading True during initial setup or scanning state changes.
 */
data class ScannerUiState(
    val devices: List<BleDevice> = emptyList(),
    val rawDevices: List<BleDevice> = emptyList(),
    val isScanning: Boolean = false,
    val isBluetoothEnabled: Boolean = true,
    val hasPermissions: Boolean = false,
    val searchQuery: String = "",
    val minRssiThreshold: Int = -100,
    val isLoading: Boolean = false,
)
