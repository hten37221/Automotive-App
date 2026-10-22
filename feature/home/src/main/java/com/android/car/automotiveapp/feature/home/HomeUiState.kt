package com.android.car.automotiveapp.feature.home

import com.android.car.automotiveapp.core.model.GreetingData

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val data: GreetingData) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
