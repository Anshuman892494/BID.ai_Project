package com.bidai.app.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: String,
    @SerializedName("fullName") val fullName: String,
    val email: String,
    val phone: String? = null,
    @SerializedName("bidCredits") val bidCredits: Int = 100,
    @SerializedName("ecoScore") val ecoScore: Int = 0,
    @SerializedName("createdAt") val createdAt: String? = null
)

data class AuthData(
    val token: String,
    val user: User
)

data class AuthResponse(
    val status: String,
    val message: String? = null,
    val data: AuthData? = null
)

data class Category(
    val id: Int,
    val name: String,
    @SerializedName("icon_name") val iconName: String? = null
)

data class CategoriesResponse(
    val status: String,
    val data: List<Category>
)

data class Product(
    val id: String,
    @SerializedName("seller_id") val sellerId: String? = null,
    @SerializedName("seller_name") val sellerName: String? = null,
    @SerializedName("category_id") val categoryId: Int? = null,
    @SerializedName("category_name") val categoryName: String? = null,
    val title: String,
    val description: String? = null,
    @SerializedName("starting_price") val startingPrice: Double,
    @SerializedName("current_bid") val currentBid: Double,
    val condition: String = "Good",
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("estimated_eco_score") val estimatedEcoScore: Int = 25,
    val status: String = "ACTIVE",
    @SerializedName("ends_at") val endsAt: String? = null,
    @SerializedName("bids_count") val bidsCount: Int = 0
)

data class ProductsResponse(
    val status: String,
    val count: Int = 0,
    val data: List<Product>
)

data class Bid(
    val id: String,
    @SerializedName("product_id") val productId: String,
    @SerializedName("bidder_id") val bidderId: String,
    @SerializedName("bidder_name") val bidderName: String? = null,
    val amount: Double,
    @SerializedName("created_at") val createdAt: String? = null
)

data class ProductDetail(
    val id: String,
    @SerializedName("seller_id") val sellerId: String? = null,
    @SerializedName("seller_name") val sellerName: String? = null,
    @SerializedName("seller_eco_score") val sellerEcoScore: Int? = null,
    @SerializedName("category_id") val categoryId: Int? = null,
    @SerializedName("category_name") val categoryName: String? = null,
    val title: String,
    val description: String? = null,
    @SerializedName("starting_price") val startingPrice: Double,
    @SerializedName("current_bid") val currentBid: Double,
    val condition: String = "Good",
    @SerializedName("image_url") val imageUrl: String? = null,
    @SerializedName("estimated_eco_score") val estimatedEcoScore: Int = 25,
    val status: String = "ACTIVE",
    @SerializedName("ends_at") val endsAt: String? = null,
    val bids: List<Bid> = emptyList()
)

data class ProductDetailResponse(
    val status: String,
    val data: ProductDetail
)

data class CreateProductRequest(
    val title: String,
    val categoryId: Int,
    val startingPrice: Double,
    val condition: String = "Like New",
    val imageUrl: String? = null,
    val description: String? = null,
    val estimatedEcoScore: Int = 50,
    val durationHours: Int = 24
)

data class CreateProductResponse(
    val status: String,
    val message: String? = null,
    val data: Product? = null
)

data class AISuggestionRequest(
    val query: String,
    val condition: String = "Like New",
    val category: String? = null
)

data class AISuggestionData(
    val suggestedTitle: String,
    val suggestedCategoryId: Int,
    val suggestedStartingPrice: Double,
    val recommendedBuyNow: Double,
    val estimatedEcoScore: Int,
    val generatedDescription: String,
    val sampleImageUrl: String? = null,
    val condition: String = "Like New",
    val confidenceScore: Double = 0.95
)

data class AISuggestionResponse(
    val status: String,
    val data: AISuggestionData
)

data class BidRequest(
    val amount: Double
)

data class BidResponse(
    val status: String,
    val message: String? = null
)
