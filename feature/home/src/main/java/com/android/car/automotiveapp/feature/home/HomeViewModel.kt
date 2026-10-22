package com.android.car.automotiveapp.feature.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.car.automotiveapp.core.common.AppLog
import com.android.car.automotiveapp.core.domain.usecase.GetCarConfigurationUseCase
import com.android.car.automotiveapp.core.domain.usecase.GetGreetingUseCase
import com.android.car.automotiveapp.core.domain.usecase.UpdateCarConfigurationUseCase
import com.android.car.automotiveapp.core.model.CarConfiguration
import com.android.car.automotiveapp.core.model.CarPart
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getGreetingUseCase: GetGreetingUseCase,
    private val getCarConfigurationUseCase: GetCarConfigurationUseCase,
    private val updateCarConfigurationUseCase: UpdateCarConfigurationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val carConfiguration: StateFlow<CarConfiguration> = getCarConfigurationUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = CarConfiguration()
        )

    // Quản lý trạng thái tab và chi tiết xe được chọn sống qua configuration change (F13, F15)
    var selectedTabIndex by mutableIntStateOf(0)
        private set

    var studioSelectedPart by mutableStateOf<CarPart?>(null)
        private set

    fun selectTab(index: Int) {
        selectedTabIndex = index
    }

    fun navigateToStudio(focusPart: CarPart? = null) {
        studioSelectedPart = focusPart
        selectedTabIndex = 1
    }

    fun selectStudioPart(part: CarPart?) {
        studioSelectedPart = part
    }

    init {
        loadGreeting()
    }

    fun loadGreeting() {
        AppLog.d("HomeViewModel", "Loading automotive greeting data")
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            getGreetingUseCase()
                .catch { exception ->
                    AppLog.e("HomeViewModel", "Error loading greeting", exception)
                    _uiState.value = HomeUiState.Error(exception.message ?: "Unknown error")
                }
                .collect { data ->
                    AppLog.i("HomeViewModel", "Greeting data loaded successfully: ${data.title}")
                    _uiState.value = HomeUiState.Success(data)
                }
        }
    }

    fun updateCarConfiguration(transform: (CarConfiguration) -> CarConfiguration) {
        viewModelScope.launch {
            updateCarConfigurationUseCase(transform)
        }
    }

    fun resetCarConfiguration() {
        viewModelScope.launch {
            updateCarConfigurationUseCase.reset()
        }
    }
}
