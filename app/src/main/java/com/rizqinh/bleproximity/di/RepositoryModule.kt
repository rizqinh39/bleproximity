package com.rizqinh.bleproximity.di

import com.rizqinh.bleproximity.data.repository.BleRepositoryImpl
import com.rizqinh.bleproximity.domain.repository.BleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module binding repository implementation to domain interface.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBleRepository(
        impl: BleRepositoryImpl
    ): BleRepository
}
