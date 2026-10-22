package com.android.car.automotiveapp.core.domain.usecase

import com.android.car.automotiveapp.core.domain.repository.GreetingRepository
import com.android.car.automotiveapp.core.model.GreetingData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGreetingUseCase @Inject constructor(
    private val greetingRepository: GreetingRepository
) {
    operator fun invoke(): Flow<GreetingData> {
        return greetingRepository.getGreeting()
    }
}
