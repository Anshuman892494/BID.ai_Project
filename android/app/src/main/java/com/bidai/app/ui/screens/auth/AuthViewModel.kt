package com.bidai.app.ui.screens.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bidai.app.data.repository.AuthRepository
import com.bidai.app.utils.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val message: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

data class AuthFormState(
    val isRegisterMode: Boolean = false,
    val fullName: String = "",
    val email: String = "demo@bid.ai", // Prefilled for swift evaluator demo
    val phone: String = "+919876543210",
    val password: String = "password123", // Prefilled for demo
    val confirmPassword: String = "password123",
    val isPasswordVisible: Boolean = false,
    val fullNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
)

class AuthViewModel(private val context: Context) : ViewModel() {
    private val repository = AuthRepository(context)

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(AuthFormState())
    val formState: StateFlow<AuthFormState> = _formState.asStateFlow()

    fun isUserLoggedIn(): Boolean = repository.isLoggedIn()

    fun onFullNameChange(value: String) {
        _formState.update { it.copy(fullName = value, fullNameError = null) }
    }

    fun onEmailChange(value: String) {
        _formState.update { it.copy(email = value, emailError = null) }
    }

    fun onPhoneChange(value: String) {
        _formState.update { it.copy(phone = value) }
    }

    fun onPasswordChange(value: String) {
        _formState.update { it.copy(password = value, passwordError = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _formState.update { it.copy(confirmPassword = value, confirmPasswordError = null) }
    }

    fun toggleRegisterMode() {
        _formState.update {
            it.copy(
                isRegisterMode = !it.isRegisterMode,
                fullNameError = null,
                emailError = null,
                passwordError = null,
                confirmPasswordError = null
            )
        }
        _uiState.value = AuthUiState.Idle
    }

    fun togglePasswordVisibility() {
        _formState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun submit(onSuccess: () -> Unit) {
        val form = _formState.value
        var hasError = false

        if (form.isRegisterMode && form.fullName.trim().isEmpty()) {
            _formState.update { it.copy(fullNameError = "Please enter your full name") }
            hasError = true
        }

        if (!ValidationUtils.isValidEmail(form.email)) {
            _formState.update { it.copy(emailError = "Please enter a valid email address") }
            hasError = true
        }

        if (!ValidationUtils.isValidPassword(form.password)) {
            _formState.update { it.copy(passwordError = "Password must be at least 6 characters") }
            hasError = true
        }

        if (form.isRegisterMode && form.password != form.confirmPassword) {
            _formState.update { it.copy(confirmPasswordError = "Passwords do not match") }
            hasError = true
        }

        if (hasError) return

        _uiState.value = AuthUiState.Loading

        viewModelScope.launch {
            if (form.isRegisterMode) {
                val result = repository.register(form.fullName, form.email, form.password, form.phone)
                result.fold(
                    onSuccess = {
                        _uiState.value = AuthUiState.Success("Account created successfully!")
                        onSuccess()
                    },
                    onFailure = { err ->
                        _uiState.value = AuthUiState.Error(err.message ?: "Registration failed")
                    }
                )
            } else {
                val result = repository.login(form.email, form.password)
                result.fold(
                    onSuccess = {
                        _uiState.value = AuthUiState.Success("Welcome back!")
                        onSuccess()
                    },
                    onFailure = { err ->
                        _uiState.value = AuthUiState.Error(err.message ?: "Invalid email or password")
                    }
                )
            }
        }
    }
}

