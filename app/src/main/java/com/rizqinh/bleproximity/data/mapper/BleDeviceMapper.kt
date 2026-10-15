package com.rizqinh.bleproximity.data.mapper

import com.rizqinh.bleproximity.core.util.RssiDistanceCalculator
import com.rizqinh.bleproximity.data.local.DeviceHistoryEntity
import com.rizqinh.bleproximity.domain.model.BleDevice
import com.rizqinh.bleproximity.domain.model.SignalCategory

/**
 * Extension mapper functions converting between domain models and Room database entities.
 */

fun BleDevice.toEntity(): DeviceHistoryEntity {
    return DeviceHistoryEntity(
        macAddress = macAddress,
        name = name,
        lastRssi = rssi,
        categoryName = category.name,
        estimatedDistanceMeters = estimatedDistanceMeters,
        lastSeenTimestamp = lastSeenTimestamp,
    )
}

fun DeviceHistoryEntity.toDomain(): BleDevice {
    val category = try {
        SignalCategory.valueOf(categoryName)
    } catch (_: Exception) {
        RssiDistanceCalculator.categorizeRssi(lastRssi)
    }

    return BleDevice(
        macAddress = macAddress,
        name = name,
        rssi = lastRssi,
        estimatedDistanceMeters = estimatedDistanceMeters,
        category = category,
        lastSeenTimestamp = lastSeenTimestamp,
    )
}
