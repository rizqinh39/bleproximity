package com.rizqinh.bleproximity.domain.repository

import com.rizqinh.bleproximity.domain.model.BleDevice
import kotlinx.coroutines.flow.Flow

/**
 * Domain repository contract defining access to BLE scanning operations
 * and persistent history logs.
 */
interface BleRepository {

    /**
     * Emits continuous stream of detected BLE devices during active scanning session.
     */
    fun scanBleDevices(): Flow<List<BleDevice>>

    /**
     * Starts active BLE scanning.
     */
    fun startScan()

    /**
     * Stops active BLE scanning.
     */
    fun stopScan()

    /**
     * Returns real-time flow of current scanning state (true when scanning).
     */
    fun isScanning(): Flow<Boolean>

    /**
     * Observes real-time system Bluetooth Adapter status (true when Bluetooth is ON).
     */
    fun isBluetoothEnabled(): Flow<Boolean>

    /**
     * Retrieves stream of stored device history logs from local database.
     */
    fun getDeviceHistory(): Flow<List<BleDevice>>

    /**
     * Upserts device log into Room database history.
     */
    suspend fun saveDeviceHistory(device: BleDevice)

    /**
     * Clears all stored device history entries.
     */
    suspend fun clearHistory()
}
