package com.android.car.automotiveapp.core.domain.usecase

import com.android.car.automotiveapp.core.domain.repository.CarRepository
import com.android.car.automotiveapp.core.model.CarConfiguration
import javax.inject.Inject

class UpdateCarConfigurationUseCase @Inject constructor(
    private val carRepository: CarRepository
) {
    suspend operator fun invoke(transform: (CarConfiguration) -> CarConfiguration) {
        carRepository.updateConfiguration(transform)
    }

    suspend fun reset() {
        carRepository.resetToDefault()
    }
}
