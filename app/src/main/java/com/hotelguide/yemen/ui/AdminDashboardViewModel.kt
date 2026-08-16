package com.hotelguide.yemen.ui.admin.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hotelguide.yemen.data.model.Hotel
import com.hotelguide.yemen.data.model.Room
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AdminDashboardUiState {
    object Loading : AdminDashboardUiState()
    data class Success(
        val hotelDocId: String,
        val hotel: Hotel,
        val rooms: List<Pair<String, Room>> // Document ID مع كل غرفة
    ) : AdminDashboardUiState()
    data class Error(val message: String) : AdminDashboardUiState()
}

class AdminDashboardViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<AdminDashboardUiState>(AdminDashboardUiState.Loading)
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    fun loadMyHotel() {
        viewModelScope.launch {
            _uiState.value = AdminDashboardUiState.Loading
            try {
                val currentUserId = Firebase.auth.currentUser?.uid
                if (currentUserId == null) {
                    _uiState.value = AdminDashboardUiState.Error("لم يتم تسجيل الدخول")
                    return@launch
                }

                val hotelSnapshot = Firebase.firestore.collection("hotels")
                    .whereEqualTo("ownerId", currentUserId)
                    .limit(1)
                    .get()
                    .await()

                val hotelDoc = hotelSnapshot.documents.firstOrNull()
                val hotel = hotelDoc?.toObject(Hotel::class.java)

                if (hotel == null || hotelDoc == null) {
                    _uiState.value = AdminDashboardUiState.Error("لا يوجد فندق مرتبط بهذا الحساب")
                    return@launch
                }

                val roomsSnapshot = Firebase.firestore.collection("rooms")
                    .whereEqualTo("hotelId", hotel.id)
                    .get()
                    .await()

                val rooms = roomsSnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Room::class.java)?.let { doc.id to it }
                }

                _uiState.value = AdminDashboardUiState.Success(hotelDoc.id, hotel, rooms)
            } catch (e: Exception) {
                _uiState.value = AdminDashboardUiState.Error(e.message ?: "خطأ غير متوقع")
            }
        }
    }

    fun logout() {
        Firebase.auth.signOut()
    }
}
