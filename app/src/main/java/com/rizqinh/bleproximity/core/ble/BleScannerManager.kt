package com.rizqinh.bleproximity.core.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import com.rizqinh.bleproximity.core.permission.BlePermissionHelper
import com.rizqinh.bleproximity.core.util.RssiDistanceCalculator
import com.rizqinh.bleproximity.domain.model.BleDevice
import com.rizqinh.bleproximity.domain.model.SignalCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Engine responsible for active Bluetooth Low Energy (BLE) scanning using [BluetoothAdapter.getBluetoothLeScanner].
 *
 * Key Features:
 * - Uses [ScanSettings.SCAN_MODE_LOW_LATENCY] for fast response.
 * - [callbackFlow] reactive stream emitting aggregated device list.
 * - 5-Second Watchdog Timer: Automatically transitions stale devices (> 5s without packets) to [SignalCategory.LOST].
 * - Signal statistics calculation (Min, Max, Avg RSSI, Packet Count, Distance).
 */
@Singleton
class BleScannerManager @Inject constructor(
    private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter?,
) {

    private val _isScanning = MutableStateFlow(value = false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    // Thread-safe map holding discovered devices by MAC address
    private val discoveredDevices = ConcurrentHashMap<String, BleDevice>()

    private var watchdogJob: Job? = null

    /**
     * Starts continuous BLE scanning and returns reactive [Flow] of detected device list.
     * Throttle emissions to UI to prevent list flicker from high-frequency packets.
     */
    @SuppressLint("MissingPermission")
    fun scanBleDevices(): Flow<List<BleDevice>> = callbackFlow {
        if (!BlePermissionHelper.hasBlePermissions(context) || (bluetoothAdapter?.isEnabled != true)) {
            _isScanning.value = false
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val scanner = bluetoothAdapter.bluetoothLeScanner
        if (scanner == null) {
            _isScanning.value = false
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                result?.let { processScanResult(it) }
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                results?.forEach { processScanResult(it) }
            }

            override fun onScanFailed(errorCode: Int) {
                _isScanning.value = false
            }
        }

        try {
            scanner.startScan(null, scanSettings, scanCallback)
            _isScanning.value = true
        } catch (_: Exception) {
            _isScanning.value = false
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        // Start 5-second Watchdog timer loop in background coroutine
        val scope = CoroutineScope(Dispatchers.Default)
        watchdogJob = scope.launch {
            while (isActive) {
                delay(1000) // Check every second
                val now = System.currentTimeMillis()
                var updated = false

                discoveredDevices.forEach { (mac, device) ->
                    // Watchdog: If device hasn't sent packets in > 5000ms, mark as LOST
                    if (now - device.lastSeenTimestamp > 5000L && device.category != SignalCategory.LOST) {
                        discoveredDevices[mac] = device.copy(
                            category = SignalCategory.LOST,
                            rssi = -999, // Indicate signal lost
                            estimatedDistanceMeters = -1.0,
                        )
                        updated = true
                    }
                }

                if (updated) {
                    trySend(discoveredDevices.values.toList())
                }
            }
        }

        // Emit initial list & emit whenever new devices are processed
        val emitterJob = scope.launch {
            while (isActive) {
                trySend(discoveredDevices.values.toList())
                delay(350) // Throttle emission to UI every 350ms
            }
        }

        awaitClose {
            try {
                scanner.stopScan(scanCallback)
            } catch (_: Exception) {
                // Ignore stop scan exception if adapter was disabled
            }
            _isScanning.value = false
            watchdogJob?.cancel()
            emitterJob.cancel()
        }
    }

    @SuppressLint("MissingPermission")
    private fun processScanResult(result: ScanResult) {
        val device = result.device ?: return
        val mac = device.address ?: return
        val rawName = device.name
        val name = if (rawName.isNullOrBlank() || rawName == "null") "Unknown BLE Device" else rawName
        val rssi = result.rssi
        val now = System.currentTimeMillis()

        val existing = discoveredDevices[mac]

        val packetCount = (existing?.packetCount ?: 0) + 1
        val minRssi = if (existing == null) rssi else kotlin.math.min(existing.minRssi, rssi)
        val maxRssi = if (existing == null) rssi else kotlin.math.max(existing.maxRssi, rssi)

        // Calculate rolling average
        val avgRssi = if (existing == null) {
            rssi.toDouble()
        } else {
            ((existing.avgRssi * existing.packetCount) + rssi) / packetCount
        }

        val distance = RssiDistanceCalculator.calculateDistance(rssi)
        val category = RssiDistanceCalculator.categorizeRssi(rssi)

        val updatedDevice = BleDevice(
            macAddress = mac,
            name = name,
            rssi = rssi,
            estimatedDistanceMeters = distance,
            category = category,
            lastSeenTimestamp = now,
            packetCount = packetCount,
            minRssi = minRssi,
            maxRssi = maxRssi,
            avgRssi = avgRssi,
        )

        discoveredDevices[mac] = updatedDevice
    }

    fun clearDiscoveredDevices() {
        discoveredDevices.clear()
    }
}
