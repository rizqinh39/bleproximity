package com.rizqinh.bleproximity.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Main Room Database configuration for BLE Proximity Tracker.
 */
@Database(
    entities = [DeviceHistoryEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class BleDatabase : RoomDatabase() {
    abstract fun deviceHistoryDao(): DeviceHistoryDao
}
