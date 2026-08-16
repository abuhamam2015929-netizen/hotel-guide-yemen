package com.hotelguide.yemen.ui.admin.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AdminLoginUiState {
    object Idle : AdminLoginUiState()
    object Loading : AdminLoginUiState()
    object Success : AdminLoginUiState()
    data class Error(val message: String) : AdminLoginUiState()
}

class AdminLoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<AdminLoginUiState>(AdminLoginUiState.Idle)
    val uiState: StateFlow<AdminLoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            if (email.isBlank() || password.isBlank()) {
                _uiState.value = AdminLoginUiState.Error("الرجاء تعبئة البريد وكلمة المرور")
                return@launch
            }
            _uiState.value = AdminLoginUiState.Loading
            try {
                Firebase.auth.signInWithEmailAndPassword(email, password).await()
                _uiState.value = AdminLoginUiState.Success
            } catch (e: Exception) {
                _uiState.value = AdminLoginUiState.Error("بيانات الدخول غير صحيحة")
            }
        }
    }
}
