package com.rizqinh.bleproximity.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for [DeviceHistoryEntity] operations in Room database.
 */
@Dao
interface DeviceHistoryDao {

    /**
     * Inserts or updates device history record.
     */
    @Upsert
    suspend fun upsertDeviceHistory(entity: DeviceHistoryEntity)

    /**
     * Retrieves all history logs sorted by last seen timestamp descending.
     */
    @Query("SELECT * FROM table_device_history ORDER BY lastSeenTimestamp DESC")
    fun getAllDeviceHistory(): Flow<List<DeviceHistoryEntity>>

    /**
     * Clears all history entries from the table.
     */
    @Query("DELETE FROM table_device_history")
    suspend fun clearAllHistory()
}
