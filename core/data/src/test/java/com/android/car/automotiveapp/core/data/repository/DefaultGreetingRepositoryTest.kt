package com.android.car.automotiveapp.core.data.repository

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DefaultGreetingRepositoryTest {

    @Test
    fun getGreeting_emitsValidGreetingData() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val repository = DefaultGreetingRepository(ioDispatcher = testDispatcher)

        val greeting = repository.getGreeting().first()

        assertThat(greeting.title).contains("Automotive")
        assertThat(greeting.architectureHighlights).isNotEmpty()
        assertThat(greeting.vehicleStatus).isNotEmpty()
    }
}
