package com.hotelguide.yemen.ui.client.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotelguide.yemen.data.model.City
import com.hotelguide.yemen.data.repository.CityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WelcomeUiState(
    val isLoading: Boolean = true,
    val cities: List<City> = emptyList(),
    val isOffline: Boolean = false
)

class WelcomeViewModel(
    private val repository: CityRepository = CityRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(WelcomeUiState())
    val uiState: StateFlow<WelcomeUiState> = _uiState.asStateFlow()

    init {
        loadCities()
    }

    fun loadCities() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.getActiveCities()
                .onSuccess { cities ->
                    _uiState.update {
                        it.copy(isLoading = false, cities = cities, isOffline = false)
                    }
                }
                .onFailure {
                    // فشل الاتصال لا يعني كسر الشاشة — نعرض ما هو متاح (Firestore
                    // يرجع البيانات المخزنة محلياً تلقائياً إن وُجدت) مع تنبيه هادئ
                    _uiState.update { current ->
                        current.copy(isLoading = false, isOffline = true)
                    }
                }
        }
    }
}
