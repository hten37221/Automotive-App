package com.android.car.automotiveapp.core.domain.repository

import com.android.car.automotiveapp.core.model.GreetingData
import kotlinx.coroutines.flow.Flow

interface GreetingRepository {
    fun getGreeting(): Flow<GreetingData>
}
