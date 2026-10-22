package com.android.car.automotiveapp.core.domain.usecase

import com.android.car.automotiveapp.core.domain.repository.CarRepository
import com.android.car.automotiveapp.core.model.CarConfiguration
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCarConfigurationUseCase @Inject constructor(
    private val carRepository: CarRepository
) {
    operator fun invoke(): Flow<CarConfiguration> {
        return carRepository.getCarConfiguration()
    }
}
