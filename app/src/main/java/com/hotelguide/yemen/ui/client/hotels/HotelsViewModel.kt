package com.hotelguide.yemen.ui.client.hotels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hotelguide.yemen.data.model.Hotel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class HotelsUiState {
    object Loading : HotelsUiState()
    object Empty : HotelsUiState()
    data class Success(val hotels: List<Hotel>) : HotelsUiState()
    data class Error(val message: String) : HotelsUiState()
}

class HotelsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<HotelsUiState>(HotelsUiState.Loading)
    val uiState: StateFlow<HotelsUiState> = _uiState.asStateFlow()

    fun loadHotels(cityId: String) {
        viewModelScope.launch {
            _uiState.value = HotelsUiState.Loading
            try {
                if (cityId.isBlank()) {
                    _uiState.value = HotelsUiState.Empty
                    return@launch
                }
                val snapshot = Firebase.firestore.collection("hotels")
                    .whereEqualTo("cityId", cityId)
                    .get()
                    .await()

                val hotels = snapshot.documents.mapNotNull { doc ->
                    try { doc.toObject(Hotel::class.java) } catch (e: Exception) { null }
                }.filter { it.isActive }

                _uiState.value = if (hotels.isEmpty()) HotelsUiState.Empty
                                  else HotelsUiState.Success(hotels)
            } catch (e: Exception) {
                _uiState.value = HotelsUiState.Error(e.message ?: "خطأ غير متوقع")
            }
        }
    }
}
