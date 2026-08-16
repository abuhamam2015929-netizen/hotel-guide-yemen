package com.hotelguide.yemen.ui.client.roomdetail

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

sealed class RoomDetailUiState {
    object Loading : RoomDetailUiState()
    data class Success(val room: Room) : RoomDetailUiState()
    data class Error(val message: String) : RoomDetailUiState()
}

class RoomDetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<RoomDetailUiState>(RoomDetailUiState.Loading)
    val uiState: StateFlow<RoomDetailUiState> = _uiState.asStateFlow()

    fun loadRoom(roomId: String) {
        viewModelScope.launch {
            _uiState.value = RoomDetailUiState.Loading
            try {
                if (roomId.isBlank()) {
                    _uiState.value = RoomDetailUiState.Error("معرف الغرفة غير صالح")
                    return@launch
                }
                val snapshot = Firebase.firestore.collection("rooms")
                    .whereEqualTo("id", roomId)
                    .limit(1)
                    .get()
                    .await()

                val room = snapshot.documents.firstOrNull()?.toObject(Room::class.java)

                _uiState.value = if (room != null) RoomDetailUiState.Success(room)
                                  else RoomDetailUiState.Error("لم يتم العثور على الغرفة")
            } catch (e: Exception) {
                _uiState.value = RoomDetailUiState.Error(e.message ?: "خطأ غير متوقع")
            }
        }
    }
}
