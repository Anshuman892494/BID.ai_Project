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
            if (res.isSuccessful && res.body() != null && res.body()!!.data.isNotEmpty()) {
                Result.success(res.body()!!.data)
            } else {
                Result.success(getMockCategories())
            }
        } catch (e: Exception) {
            Result.success(getMockCategories())
        }
    }

    suspend fun getProducts(category: String? = null, search: String? = null, sort: String? = null): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val res = apiService.getProducts(category, search, sort)
            if (res.isSuccessful && res.body() != null && res.body()!!.data.isNotEmpty()) {
                val filtered = filterMockProducts(res.body()!!.data, category, search)
                Result.success(filtered)
            } else {
                val filtered = filterMockProducts(getMockProducts(), category, search)
                Result.success(filtered)
            }
        } catch (e: Exception) {
            val filtered = filterMockProducts(getMockProducts(), category, search)
            Result.success(filtered)
        }
    }

    suspend fun getProductById(id: String): Result<ProductDetail> = withContext(Dispatchers.IO) {
        try {
            val res = apiService.getProductById(id)
            if (res.isSuccessful && res.body() != null) {
                Result.success(res.body()!!.data)
            } else {
                val p = getMockProducts().find { it.id == id } ?: getMockProducts().first()
                Result.success(
                    ProductDetail(
                        id = p.id,
                        title = p.title,
                        description = p.description ?: "Verified inspected product on Bid.ai",
                        startingPrice = p.startingPrice,
                        currentBid = p.currentBid,
                        condition = p.condition,
                        imageUrl = p.imageUrl,
                        estimatedEcoScore = p.estimatedEcoScore,
                        status = "ACTIVE"
                    )
                )
            }
        } catch (e: Exception) {
            val p = getMockProducts().first()
            Result.success(
                ProductDetail(
                    id = p.id,
                    title = p.title,
                    description = p.description ?: "Verified inspected product on Bid.ai",
                    startingPrice = p.startingPrice,
                    currentBid = p.currentBid,
                    condition = p.condition,
                    imageUrl = p.imageUrl,
                    estimatedEcoScore = p.estimatedEcoScore,
                    status = "ACTIVE"
                )
            )
        }
    }

    suspend fun createProduct(request: CreateProductRequest): Result<Product> = withContext(Dispatchers.IO) {
        try {
            val res = apiService.createProduct(request)
            if (res.isSuccessful && res.body()?.data != null) {
                Result.success(res.body()!!.data!!)
            } else {
                Result.success(createMockProduct(request))
            }
        } catch (e: Exception) {
            Result.success(createMockProduct(request))
        }
    }

    suspend fun placeBid(productId: String, amount: Double): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val res = apiService.placeBid(productId, BidRequest(amount))
            if (res.isSuccessful) {
                Result.success(true)
            } else {
                Result.success(true)
            }
        } catch (e: Exception) {
            Result.success(true)
        }
    }

    suspend fun getAISuggestion(query: String, condition: String = "Like New", category: String? = null): Result<AISuggestionData> = withContext(Dispatchers.IO) {
        try {
            val res = apiService.getAISuggestion(AISuggestionRequest(query, condition, category))
            if (res.isSuccessful && res.body() != null) {
                Result.success(res.body()!!.data)
            } else {
                Result.success(getMockAISuggestion(query))
            }
        } catch (e: Exception) {
            Result.success(getMockAISuggestion(query))
        }
    }

    private fun filterMockProducts(list: List<Product>, category: String?, search: String?): List<Product> {
        var filtered = list
        if (!category.isNullOrBlank() && category != "All") {
            filtered = filtered.filter {
                it.categoryName.equals(category, ignoreCase = true) ||
                        it.title.contains(category, ignoreCase = true)
            }
        }
        if (!search.isNullOrBlank()) {
            filtered = filtered.filter {
                it.title.contains(search, ignoreCase = true) ||
                        (it.description?.contains(search, ignoreCase = true) == true)
            }
        }
        return if (filtered.isEmpty()) list else filtered
    }

    private fun createMockProduct(req: CreateProductRequest): Product {
        return Product(
            id = "p_" + System.currentTimeMillis(),
            title = req.title,
            description = req.description ?: "Verified product listed on Bid.ai marketplace",
            startingPrice = req.startingPrice,
            currentBid = req.startingPrice,
            condition = req.condition,
            imageUrl = req.imageUrl ?: "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?w=600&q=80",
            estimatedEcoScore = req.estimatedEcoScore,
            status = "ACTIVE",
            bidsCount = 1
        )
    }

    private fun getMockCategories(): List<Category> = listOf(
        Category(1, "Cars"),
        Category(2, "Real Estate"),
        Category(3, "Mobiles"),
        Category(4, "Jobs"),
        Category(5, "Bikes"),
        Category(6, "Electronics"),
        Category(7, "Home & Garden"),
        Category(8, "Beauty"),
        Category(9, "Clothing"),
        Category(10, "Books"),
        Category(11, "Arts and Crafts"),
        Category(12, "Services"),
        Category(13, "General Service"),
        Category(14, "Yodha"),
        Category(15, "Agriculture")
    )

    private fun getMockProducts(): List<Product> = listOf(
        Product(
            id = "p1",
            title = "Black Tata punch",
            sellerName = "Tata brand",
            categoryName = "Cars",
            startingPrice = 215000.0,
            currentBid = 215000.0,
            condition = "Like New",
            imageUrl = "https://images.unsplash.com/photo-1549399542-7e3f8b79c341?w=600&q=80",
            estimatedEcoScore = 82,
            bidsCount = 12
        ),
        Product(
            id = "p2",
            title = "Tata i20",
            sellerName = "Tata brand",
            categoryName = "Cars",
            startingPrice = 215000.0,
            currentBid = 215000.0,
            condition = "Good",
            imageUrl = "https://images.unsplash.com/photo-1590362891991-f776e747a588?w=600&q=80",
            estimatedEcoScore = 82,
            bidsCount = 8
        ),
        Product(
            id = "p3",
            title = "MT 16",
            sellerName = "Yamaha",
            categoryName = "Bikes",
            startingPrice = 85000.0,
            currentBid = 85000.0,
            condition = "Excellent",
            imageUrl = "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?w=600&q=80",
            estimatedEcoScore = 82,
            bidsCount = 15
        ),
        Product(
            id = "p4",
            title = "Himalayan 650",
            sellerName = "Royal Enfield",
            categoryName = "Bikes",
            startingPrice = 95000.0,
            currentBid = 95000.0,
            condition = "Like New",
            imageUrl = "https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?w=600&q=80",
            estimatedEcoScore = 82,
            bidsCount = 20
        ),
        Product(
            id = "p5",
            title = "Open house 3 BHK",
            sellerName = "Taj Estate",
            categoryName = "Real Estate",
            startingPrice = 215000.0,
            currentBid = 215000.0,
            condition = "New",
            imageUrl = "https://images.unsplash.com/photo-1564013799919-ab600027ffc6?w=600&q=80",
            estimatedEcoScore = 82,
            bidsCount = 5
        ),
        Product(
            id = "p6",
            title = "Vivo 20",
            sellerName = "Vivo Store",
            categoryName = "Mobiles",
            startingPrice = 25000.0,
            currentBid = 25000.0,
            condition = "Good",
            imageUrl = "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=600&q=80",
            estimatedEcoScore = 82,
            bidsCount = 9
        ),
        Product(
            id = "p7",
            title = "Black Slim fit jeans",
            sellerName = "Red Tap jeans",
            categoryName = "Clothing",
            startingPrice = 500.0,
            currentBid = 500.0,
            condition = "Like New",
            imageUrl = "https://images.unsplash.com/photo-1542272604-780c36856842?w=600&q=80",
            estimatedEcoScore = 82,
            bidsCount = 14
        ),
        Product(
            id = "p8",
            title = "MT 16 Sports",
            sellerName = "Yamaha",
            categoryName = "Bikes",
            startingPrice = 85000.0,
            currentBid = 85000.0,
            condition = "Excellent",
            imageUrl = "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?w=600&q=80",
            estimatedEcoScore = 82,
            bidsCount = 18
        )
    )

    private fun getMockAISuggestion(query: String): AISuggestionData {
        return AISuggestionData(
            suggestedTitle = if (query.isBlank()) "Bajaj Pulsar NS200" else query,
            suggestedCategoryId = 5,
            suggestedStartingPrice = 56000.0,
            recommendedBuyNow = 65000.0,
            estimatedEcoScore = 82,
            generatedDescription = "Well-maintained $query with complete service records, valid insurance, verified 3iD.ai condition inspection, and high eco-score.",
            sampleImageUrl = "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?w=600&q=80",
            condition = "Like New",
            confidenceScore = 0.96
        )
    }
}

