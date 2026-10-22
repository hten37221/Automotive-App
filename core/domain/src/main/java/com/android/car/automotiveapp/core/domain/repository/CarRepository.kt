package com.android.car.automotiveapp.core.domain.repository

import com.android.car.automotiveapp.core.model.CarConfiguration
import kotlinx.coroutines.flow.Flow

interface CarRepository {
    fun getCarConfiguration(): Flow<CarConfiguration>
    suspend fun updateConfiguration(transform: (CarConfiguration) -> CarConfiguration)
    suspend fun resetToDefault()
}
