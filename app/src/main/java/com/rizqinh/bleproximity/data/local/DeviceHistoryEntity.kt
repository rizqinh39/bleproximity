package com.rizqinh.bleproximity.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Database entity table for storing historical device scanning records.
 */
@Entity(tableName = "table_device_history")
data class DeviceHistoryEntity(
    @PrimaryKey
    val macAddress: String,
    val name: String,
    val lastRssi: Int,
    val categoryName: String,
    val estimatedDistanceMeters: Double,
    val lastSeenTimestamp: Long,
)
