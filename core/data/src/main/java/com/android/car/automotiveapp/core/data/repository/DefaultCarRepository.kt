package com.android.car.automotiveapp.core.data.repository

import com.android.car.automotiveapp.core.data.datasource.CarPreferencesDataSource
import com.android.car.automotiveapp.core.domain.repository.CarRepository
import com.android.car.automotiveapp.core.model.CarConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultCarRepository @Inject constructor(
    private val preferencesDataSource: CarPreferencesDataSource
) : CarRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _carConfiguration = MutableStateFlow(CarConfiguration())
    val carConfigurationState: Flow<CarConfiguration> = _carConfiguration.asStateFlow()

    init {
        repositoryScope.launch {
            val savedConfig = preferencesDataSource.loadConfiguration()
            _carConfiguration.value = savedConfig
        }
    }

    override fun getCarConfiguration(): Flow<CarConfiguration> = carConfigurationState

    override suspend fun updateConfiguration(transform: (CarConfiguration) -> CarConfiguration) {
        _carConfiguration.update(transform)
        preferencesDataSource.saveConfiguration(_carConfiguration.value)
    }

    override suspend fun resetToDefault() {
        val defaultConfig = CarConfiguration()
        _carConfiguration.value = defaultConfig
        preferencesDataSource.clearConfiguration()
    }
}
