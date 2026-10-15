package com.rizqinh.bleproximity.data.repository

import com.rizqinh.bleproximity.core.ble.BleScannerManager
import com.rizqinh.bleproximity.core.ble.BluetoothStateReceiver
import com.rizqinh.bleproximity.data.local.DeviceHistoryDao
import com.rizqinh.bleproximity.data.mapper.toDomain
import com.rizqinh.bleproximity.data.mapper.toEntity
import com.rizqinh.bleproximity.domain.model.BleDevice
import com.rizqinh.bleproximity.domain.repository.BleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [BleRepository] providing data stream integration between BLE hardware scanner,
 * Bluetooth adapter state listener, and Room database persistence.
 */
@Singleton
class BleRepositoryImpl @Inject constructor(
    private val scannerManager: BleScannerManager,
    private val stateReceiver: BluetoothStateReceiver,
    private val historyDao: DeviceHistoryDao,
) : BleRepository {

    override fun scanBleDevices(): Flow<List<BleDevice>> {
        return scannerManager.scanBleDevices()
    }

    override fun startScan() {
        // Controlled reactively inside scanBleDevices flow scope
    }

    override fun stopScan() {
        // Controlled reactively inside scanBleDevices flow scope
    }

    override fun isScanning(): Flow<Boolean> {
        return scannerManager.isScanning
    }

    override fun isBluetoothEnabled(): Flow<Boolean> {
        return stateReceiver.observeBluetoothState()
    }

    override fun getDeviceHistory(): Flow<List<BleDevice>> {
        return historyDao.getAllDeviceHistory().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveDeviceHistory(device: BleDevice) {
        historyDao.upsertDeviceHistory(device.toEntity())
    }

    override suspend fun clearHistory() {
        historyDao.clearAllHistory()
    }
}
