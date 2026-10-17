package com.bidai.app.data.api

import com.bidai.app.data.model.AISuggestionRequest
import com.bidai.app.data.model.AISuggestionResponse
import com.bidai.app.data.model.AuthResponse
import com.bidai.app.data.model.BidRequest
import com.bidai.app.data.model.BidResponse
import com.bidai.app.data.model.CategoriesResponse
import com.bidai.app.data.model.CreateProductRequest
import com.bidai.app.data.model.CreateProductResponse
import com.bidai.app.data.model.ProductDetailResponse
import com.bidai.app.data.model.ProductsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("api/auth/register")
    suspend fun register(@Body body: Map<String, String>): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body body: Map<String, String>): Response<AuthResponse>

    @GET("api/products/categories")
    suspend fun getCategories(): Response<CategoriesResponse>

    @GET("api/products")
    suspend fun getProducts(
        @Query("category") category: String? = null,
        @Query("search") search: String? = null,
        @Query("sort") sort: String? = null
    ): Response<ProductsResponse>

    @GET("api/products/{id}")
    suspend fun getProductById(@Path("id") id: String): Response<ProductDetailResponse>

    @POST("api/products")
    suspend fun createProduct(@Body body: CreateProductRequest): Response<CreateProductResponse>

    @POST("api/products/{id}/bid")
    suspend fun placeBid(
        @Path("id") id: String,
        @Body body: BidRequest
    ): Response<BidResponse>

    @POST("api/products/ai-suggest")
    suspend fun getAISuggestion(@Body body: AISuggestionRequest): Response<AISuggestionResponse>
}
