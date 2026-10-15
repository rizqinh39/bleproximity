package com.rizqinh.bleproximity.di

import android.content.Context
import androidx.room.Room
import com.rizqinh.bleproximity.data.local.BleDatabase
import com.rizqinh.bleproximity.data.local.DeviceHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module configuring Room Database instance and Dao injection.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideBleDatabase(@ApplicationContext context: Context): BleDatabase {
        return Room.databaseBuilder(
            context,
            BleDatabase::class.java,
            "ble_proximity.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideDeviceHistoryDao(database: BleDatabase): DeviceHistoryDao {
        return database.deviceHistoryDao()
    }
}
