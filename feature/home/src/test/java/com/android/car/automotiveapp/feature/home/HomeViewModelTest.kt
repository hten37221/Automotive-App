package com.android.car.automotiveapp.feature.home

import app.cash.turbine.test
import com.android.car.automotiveapp.core.domain.repository.CarRepository
import com.android.car.automotiveapp.core.domain.repository.GreetingRepository
import com.android.car.automotiveapp.core.domain.usecase.GetCarConfigurationUseCase
import com.android.car.automotiveapp.core.domain.usecase.GetGreetingUseCase
import com.android.car.automotiveapp.core.domain.usecase.UpdateCarConfigurationUseCase
import com.android.car.automotiveapp.core.model.CarColor
import com.android.car.automotiveapp.core.model.CarConfiguration
import com.android.car.automotiveapp.core.model.GreetingData
import com.android.car.automotiveapp.core.model.HeadlightMode
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_initialStateLoadsGreetingSuccessfully() = runTest {
        val testData = GreetingData(
            title = "Test Hello Automotive",
            subtitle = "Test Subtitle",
            architectureHighlights = listOf("Highlight 1", "Highlight 2"),
            vehicleStatus = "ONLINE",
            timestamp = 1000L
        )

        val fakeGreetingRepository = object : GreetingRepository {
            override fun getGreeting() = flowOf(testData)
        }
        val getGreetingUseCase = GetGreetingUseCase(fakeGreetingRepository)

        val carConfigFlow = MutableStateFlow(CarConfiguration())
        val fakeCarRepository = object : CarRepository {
            override fun getCarConfiguration(): Flow<CarConfiguration> = carConfigFlow.asStateFlow()
            override suspend fun updateConfiguration(transform: (CarConfiguration) -> CarConfiguration) {
                carConfigFlow.update(transform)
            }
            override suspend fun resetToDefault() {
                carConfigFlow.value = CarConfiguration()
            }
        }
        val getCarConfigUseCase = GetCarConfigurationUseCase(fakeCarRepository)
        val updateCarConfigUseCase = UpdateCarConfigurationUseCase(fakeCarRepository)

        val viewModel = HomeViewModel(
            getGreetingUseCase = getGreetingUseCase,
            getCarConfigurationUseCase = getCarConfigUseCase,
            updateCarConfigurationUseCase = updateCarConfigUseCase
        )

        viewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(HomeUiState.Loading)
            testScheduler.advanceUntilIdle()
            val state = awaitItem()
            assertThat(state).isInstanceOf(HomeUiState.Success::class.java)
            val success = state as HomeUiState.Success
            assertThat(success.data.title).isEqualTo("Test Hello Automotive")
        }
    }

    @Test
    fun carConfiguration_updatesSuccessfully() = runTest {
        val testData = GreetingData(
            title = "Automotive",
            subtitle = "Sub",
            architectureHighlights = emptyList(),
            vehicleStatus = "ONLINE",
            timestamp = 1000L
        )
        val fakeGreetingRepository = object : GreetingRepository {
            override fun getGreeting() = flowOf(testData)
        }
        val getGreetingUseCase = GetGreetingUseCase(fakeGreetingRepository)

        val carConfigFlow = MutableStateFlow(CarConfiguration())
        val fakeCarRepository = object : CarRepository {
            override fun getCarConfiguration(): Flow<CarConfiguration> = carConfigFlow.asStateFlow()
            override suspend fun updateConfiguration(transform: (CarConfiguration) -> CarConfiguration) {
                carConfigFlow.update(transform)
            }
            override suspend fun resetToDefault() {
                carConfigFlow.value = CarConfiguration()
            }
        }
        val getCarConfigUseCase = GetCarConfigurationUseCase(fakeCarRepository)
        val updateCarConfigUseCase = UpdateCarConfigurationUseCase(fakeCarRepository)

        val viewModel = HomeViewModel(
            getGreetingUseCase = getGreetingUseCase,
            getCarConfigurationUseCase = getCarConfigUseCase,
            updateCarConfigurationUseCase = updateCarConfigUseCase
        )

        viewModel.carConfiguration.test {
            assertThat(awaitItem()).isEqualTo(CarConfiguration())

            viewModel.updateCarConfiguration {
                it.copy(
                    bodyColor = CarColor.RACING_RED,
                    headlightMode = HeadlightMode.HIGH_BEAM,
                    trunkOpenPercent = 1.0f
                )
            }
            testScheduler.advanceUntilIdle()

            val updated = awaitItem()
            assertThat(updated.bodyColor).isEqualTo(CarColor.RACING_RED)
            assertThat(updated.headlightMode).isEqualTo(HeadlightMode.HIGH_BEAM)
            assertThat(updated.trunkOpenPercent).isEqualTo(1.0f)
        }
    }

    @Test
    fun navigationAndSelection_preservesState() = runTest {
        val fakeGreetingRepo = object : GreetingRepository {
            override fun getGreeting() = flowOf(GreetingData("Title", "Sub", emptyList(), "ONLINE", 1000L))
        }
        val fakeCarRepo = object : CarRepository {
            val flow = MutableStateFlow(CarConfiguration())
            override fun getCarConfiguration() = flow
            override suspend fun updateConfiguration(transform: (CarConfiguration) -> CarConfiguration) {
                flow.update(transform)
            }
            override suspend fun resetToDefault() {
                flow.value = CarConfiguration()
            }
        }

        val viewModel = HomeViewModel(
            getGreetingUseCase = GetGreetingUseCase(fakeGreetingRepo),
            getCarConfigurationUseCase = GetCarConfigurationUseCase(fakeCarRepo),
            updateCarConfigurationUseCase = UpdateCarConfigurationUseCase(fakeCarRepo)
        )

        assertThat(viewModel.selectedTabIndex).isEqualTo(0)
        assertThat(viewModel.studioSelectedPart).isNull()

        // Điều hướng từ Dashboard sang Studio với part cụ thể (F13)
        viewModel.navigateToStudio(com.android.car.automotiveapp.core.model.CarPart.WHEELS)
        assertThat(viewModel.selectedTabIndex).isEqualTo(1)
        assertThat(viewModel.studioSelectedPart).isEqualTo(com.android.car.automotiveapp.core.model.CarPart.WHEELS)

        // Reset cấu hình (F16)
        viewModel.updateCarConfiguration { it.copy(bodyColor = CarColor.EMERALD_GREEN) }
        testScheduler.advanceUntilIdle()
        assertThat(viewModel.carConfiguration.value.bodyColor).isEqualTo(CarColor.EMERALD_GREEN)

        viewModel.resetCarConfiguration()
        testScheduler.advanceUntilIdle()
        assertThat(viewModel.carConfiguration.value.bodyColor).isEqualTo(CarColor.CYBER_CYAN)
    }
}
