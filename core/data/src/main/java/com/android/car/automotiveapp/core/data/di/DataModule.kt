package com.android.car.automotiveapp.core.data.di

import com.android.car.automotiveapp.core.data.datasource.CarPreferencesDataSource
import com.android.car.automotiveapp.core.data.datasource.FileCarPreferencesDataSource
import com.android.car.automotiveapp.core.data.repository.DefaultCarRepository
import com.android.car.automotiveapp.core.data.repository.DefaultGreetingRepository
import com.android.car.automotiveapp.core.domain.repository.CarRepository
import com.android.car.automotiveapp.core.domain.repository.GreetingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindGreetingRepository(
        defaultGreetingRepository: DefaultGreetingRepository
    ): GreetingRepository

    @Binds
    @Singleton
    abstract fun bindCarRepository(
        defaultCarRepository: DefaultCarRepository
    ): CarRepository

    @Binds
    @Singleton
    abstract fun bindCarPreferencesDataSource(
        fileCarPreferencesDataSource: FileCarPreferencesDataSource
    ): CarPreferencesDataSource
}
