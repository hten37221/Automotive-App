package com.android.car.automotiveapp.core.data.repository

import com.android.car.automotiveapp.core.common.AutomotiveDispatchers
import com.android.car.automotiveapp.core.common.Dispatcher
import com.android.car.automotiveapp.core.domain.repository.GreetingRepository
import com.android.car.automotiveapp.core.model.GreetingData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DefaultGreetingRepository @Inject constructor(
    @param:Dispatcher(AutomotiveDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : GreetingRepository {

    override fun getGreeting(): Flow<GreetingData> = flow {
        val greeting = GreetingData(
            title = "Hello Automotive World!",
            subtitle = "Android Automotive Clean Architecture & Multi-Module",
            architectureHighlights = listOf(
                "Clean Architecture: :core (model, domain, data, common, designsystem)",
                "Feature Modularization: :feature:home",
                "Composite Build Logic Convention Plugins",
                "Dependency Injection: Dagger Hilt + KSP",
                "Reactive UI: Jetpack Compose + ViewModel + StateFlow",
                "Navigation: Navigation Compose"
            ),
            vehicleStatus = "SYSTEM READY • ENGINE IDLE • ALL SENSORS NOMINAL",
            timestamp = System.currentTimeMillis()
        )
        emit(greeting)
    }.flowOn(ioDispatcher)
}
