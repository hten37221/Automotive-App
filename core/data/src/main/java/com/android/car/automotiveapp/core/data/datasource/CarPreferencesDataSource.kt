package com.android.car.automotiveapp.core.data.datasource

import com.android.car.automotiveapp.core.model.CarConfiguration

interface CarPreferencesDataSource {
    suspend fun loadConfiguration(): CarConfiguration
    suspend fun saveConfiguration(config: CarConfiguration)
    suspend fun clearConfiguration()
}
