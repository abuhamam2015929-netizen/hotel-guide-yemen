package com.hotelguide.yemen.ui.client.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hotelguide.yemen.data.model.Hotel
import com.hotelguide.yemen.data.model.Room
import com.hotelguide.yemen.data.model.RoomStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class PaymentUiState {
    object Loading : PaymentUiState()
    data class Success(val hotel: Hotel, val room: Room) : PaymentUiState()
    data class Error(val message: String) : PaymentUiState()
}

class PaymentViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<PaymentUiState>(PaymentUiState.Loading)
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    private val _bookingConfirmed = MutableStateFlow(false)
    val bookingConfirmed: StateFlow<Boolean> = _bookingConfirmed.asStateFlow()

    fun loadData(hotelId: String, roomId: String) {
        viewModelScope.launch {
            _uiState.value = PaymentUiState.Loading
            try {
                val hotelSnapshot = Firebase.firestore.collection("hotels")
                    .whereEqualTo("id", hotelId)
                    .limit(1)
                    .get()
                    .await()

                val roomSnapshot = Firebase.firestore.collection("rooms")
                    .whereEqualTo("id", roomId)
                    .limit(1)
                    .get()
                    .await()

                val hotel = hotelSnapshot.documents.firstOrNull()?.toObject(Hotel::class.java)
                val room = roomSnapshot.documents.firstOrNull()?.toObject(Room::class.java)

                if (hotel != null && room != null) {
                    _uiState.value = PaymentUiState.Success(hotel, room)
                } else {
                    _uiState.value = PaymentUiState.Error("تعذر العثور على بيانات الفندق أو الغرفة")
                }
            } catch (e: Exception) {
                _uiState.value = PaymentUiState.Error(e.message ?: "خطأ غير متوقع")
            }
        }
    }

    fun markRoomAsPending(roomDocId: String, onDone: () -> Unit) {
        viewModelScope.launch {
            try {
                Firebase.firestore.collection("rooms")
                    .document(roomDocId)
                    .update("status", RoomStatus.PENDING.name)
                    .await()
                _bookingConfirmed.value = true
            } catch (e: Exception) {
                // حتى لو فشل التحديث، نكمل فتح واتساب (لا نعطّل تجربة المستخدم)
            } finally {
                onDone()
            }
        }
    }
}
