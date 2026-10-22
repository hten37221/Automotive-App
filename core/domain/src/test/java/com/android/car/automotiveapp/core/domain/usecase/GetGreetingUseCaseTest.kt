package com.android.car.automotiveapp.core.domain.usecase

import com.android.car.automotiveapp.core.domain.repository.GreetingRepository
import com.android.car.automotiveapp.core.model.GreetingData
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GetGreetingUseCaseTest {

    @Test
    fun invoke_returnsGreetingDataFromRepository() = runTest {
        val testData = GreetingData(
            title = "Test Title",
            subtitle = "Test Subtitle",
            architectureHighlights = listOf("Highlight 1"),
            vehicleStatus = "NOMINAL",
            timestamp = 12345L
        )

        val fakeRepository = object : GreetingRepository {
            override fun getGreeting() = flowOf(testData)
        }

        val useCase = GetGreetingUseCase(fakeRepository)
        val result = useCase().first()

        assertThat(result).isEqualTo(testData)
    }
}
