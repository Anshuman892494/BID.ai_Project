package com.bidai.app.data.repository

import android.content.Context
import com.bidai.app.data.api.ApiClient
import com.bidai.app.data.api.ApiService
import com.bidai.app.data.model.AuthResponse
import com.bidai.app.utils.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val context: Context) {
    private val apiService: ApiService = ApiClient.getService(context)
    private val tokenManager: TokenManager = TokenManager.getInstance(context)

    suspend fun login(email: String, password: String): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val body = mapOf("email" to email.trim(), "password" to password)
            val response = apiService.login(body)
            if (response.isSuccessful && response.body() != null) {
                val authRes = response.body()!!
                authRes.data?.let { data ->
                    tokenManager.saveAuthData(
                        token = data.token,
                        id = data.user.id,
                        name = data.user.fullName,
                        email = data.user.email,
                        credits = data.user.bidCredits,
                        eco = data.user.ecoScore
                    )
                }
                Result.success(authRes)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Login failed"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(fullName: String, email: String, password: String, phone: String?): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val body = mutableMapOf(
                "fullName" to fullName.trim(),
                "email" to email.trim(),
                "password" to password
            )
            if (!phone.isNullOrBlank()) body["phone"] = phone.trim()

            val response = apiService.register(body)
            if (response.isSuccessful && response.body() != null) {
                val authRes = response.body()!!
                authRes.data?.let { data ->
                    tokenManager.saveAuthData(
                        token = data.token,
                        id = data.user.id,
                        name = data.user.fullName,
                        email = data.user.email,
                        credits = data.user.bidCredits,
                        eco = data.user.ecoScore
                    )
                }
                Result.success(authRes)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Registration failed"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isLoggedIn(): Boolean = tokenManager.isLoggedIn()

    fun logout() {
        tokenManager.clearAuth()
    }
}
