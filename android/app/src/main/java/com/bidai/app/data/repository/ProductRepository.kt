package com.bidai.app.data.repository

import android.content.Context
import com.bidai.app.data.api.ApiClient
import com.bidai.app.data.api.ApiService
import com.bidai.app.data.model.AISuggestionData
import com.bidai.app.data.model.AISuggestionRequest
import com.bidai.app.data.model.BidRequest
import com.bidai.app.data.model.Category
import com.bidai.app.data.model.CreateProductRequest
import com.bidai.app.data.model.Product
import com.bidai.app.data.model.ProductDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepository(context: Context) {
    private val apiService: ApiService = ApiClient.getService(context)

    suspend fun getCategories(): Result<List<Category>> = withContext(Dispatchers.IO) {
        try {
            val res = apiService.getCategories()
            if (res.isSuccessful && res.body() != null) {
                Result.success(res.body()!!.data)
            } else {
                Result.failure(Exception("Failed to load categories"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProducts(category: String? = null, search: String? = null, sort: String? = null): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val res = apiService.getProducts(category, search, sort)
            if (res.isSuccessful && res.body() != null) {
                Result.success(res.body()!!.data)
            } else {
                Result.failure(Exception("Failed to load products"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductById(id: String): Result<ProductDetail> = withContext(Dispatchers.IO) {
        try {
            val res = apiService.getProductById(id)
            if (res.isSuccessful && res.body() != null) {
                Result.success(res.body()!!.data)
            } else {
                Result.failure(Exception("Failed to load product details"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createProduct(request: CreateProductRequest): Result<Product> = withContext(Dispatchers.IO) {
        try {
            val res = apiService.createProduct(request)
            if (res.isSuccessful && res.body()?.data != null) {
                Result.success(res.body()!!.data!!)
            } else {
                val errorMsg = res.errorBody()?.string() ?: "Failed to list product"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun placeBid(productId: String, amount: Double): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val res = apiService.placeBid(productId, BidRequest(amount))
            if (res.isSuccessful) {
                Result.success(true)
            } else {
                val errorMsg = res.errorBody()?.string() ?: "Failed to place bid"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAISuggestion(query: String, condition: String = "Like New", category: String? = null): Result<AISuggestionData> = withContext(Dispatchers.IO) {
        try {
            val res = apiService.getAISuggestion(AISuggestionRequest(query, condition, category))
            if (res.isSuccessful && res.body() != null) {
                Result.success(res.body()!!.data)
            } else {
                Result.failure(Exception("AI Auto-fill recommendation unavailable"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
