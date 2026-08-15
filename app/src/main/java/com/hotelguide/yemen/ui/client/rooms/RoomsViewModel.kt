package com.hotelguide.yemen.ui.client.rooms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hotelguide.yemen.data.model.Room
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class RoomsUiState {
    object Loading : RoomsUiState()
    object Empty : RoomsUiState()
    data class Success(val rooms: List<Room>) : RoomsUiState()
    data class Error(val message: String) : RoomsUiState()
}

class RoomsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<RoomsUiState>(RoomsUiState.Loading)
    val uiState: StateFlow<RoomsUiState> = _uiState.asStateFlow()

    fun loadRooms(hotelId: String) {
        viewModelScope.launch {
            _uiState.value = RoomsUiState.Loading
            try {
                if (hotelId.isBlank()) {
                    _uiState.value = RoomsUiState.Empty
                    return@launch
                }
                val snapshot = Firebase.firestore.collection("rooms")
                    .whereEqualTo("hotelId", hotelId)
                    .get()
                    .await()

                val rooms = snapshot.documents.mapNotNull { doc ->
                    try { doc.toObject(Room::class.java) } catch (e: Exception) { null }
                }

                _uiState.value = if (rooms.isEmpty()) RoomsUiState.Empty
                                  else RoomsUiState.Success(rooms)
            } catch (e: Exception) {
                _uiState.value = RoomsUiState.Error(e.message ?: "خطأ غير متوقع")
            }
        }
    }
}
