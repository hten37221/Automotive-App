package com.android.car.automotiveapp.core.data.repository

import com.android.car.automotiveapp.core.data.datasource.CarPreferencesDataSource
import com.android.car.automotiveapp.core.model.CarColor
import com.android.car.automotiveapp.core.model.CarConfiguration
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultCarRepositoryTest {

    private class FakeCarPreferencesDataSource(
        var inMemoryConfig: CarConfiguration = CarConfiguration()
    ) : CarPreferencesDataSource {
        var saveCount = 0
        var clearCount = 0

        override suspend fun loadConfiguration(): CarConfiguration = inMemoryConfig

        override suspend fun saveConfiguration(config: CarConfiguration) {
            inMemoryConfig = config
            saveCount++
        }

        override suspend fun clearConfiguration() {
            inMemoryConfig = CarConfiguration()
            clearCount++
        }
    }

    @Test
    fun updateConfiguration_updatesStateAndPersists() = runTest {
        val fakeDataSource = FakeCarPreferencesDataSource()
        val repository = DefaultCarRepository(fakeDataSource)

        repository.updateConfiguration { it.copy(bodyColor = CarColor.RACING_RED) }

        val current = repository.getCarConfiguration().first()
        assertThat(current.bodyColor).isEqualTo(CarColor.RACING_RED)
        assertThat(fakeDataSource.inMemoryConfig.bodyColor).isEqualTo(CarColor.RACING_RED)
        assertThat(fakeDataSource.saveCount).isEqualTo(1)
    }

    @Test
    fun resetToDefault_restoresInitialStateAndClearsStorage() = runTest {
        val customConfig = CarConfiguration(bodyColor = CarColor.ELECTRIC_GOLD, mirrorsFolded = true)
        val fakeDataSource = FakeCarPreferencesDataSource(customConfig)
        val repository = DefaultCarRepository(fakeDataSource)

        repository.resetToDefault()

        val current = repository.getCarConfiguration().first()
        assertThat(current.bodyColor).isEqualTo(CarColor.CYBER_CYAN)
        assertThat(current.mirrorsFolded).isFalse()
        assertThat(fakeDataSource.clearCount).isEqualTo(1)
    }
}
