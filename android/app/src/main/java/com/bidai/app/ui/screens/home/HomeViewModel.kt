package com.bidai.app.ui.screens.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bidai.app.data.model.Category
import com.bidai.app.data.model.Product
import com.bidai.app.data.repository.ProductRepository
import com.bidai.app.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val categories: List<Category> = emptyList(),
    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val products: List<Product> = emptyList(),
    val errorMessage: String? = null,
    val bidSuccessMessage: String? = null
)

class HomeViewModel(private val context: Context) : ViewModel() {
    private val repository = ProductRepository(context)
    private val tokenManager = TokenManager.getInstance(context)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val userName: String get() = tokenManager.getUserName()
    val userCredits: Int get() = tokenManager.getCredits()
    val userEcoScore: Int get() = tokenManager.getEcoScore()

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            // Load Categories
            repository.getCategories().onSuccess { cats ->
                _uiState.update { it.copy(categories = cats) }
            }

            // Load Products
            fetchProducts()
        }
    }

    fun onCategorySelect(categoryName: String) {
        _uiState.update { it.copy(selectedCategory = categoryName) }
        fetchProducts()
    }

    fun onSearchChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        fetchProducts()
    }

    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true) }
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            val state = _uiState.value
            val categoryParam = if (state.selectedCategory == "All") null else state.selectedCategory
            val searchParam = if (state.searchQuery.isBlank()) null else state.searchQuery

            val result = repository.getProducts(category = categoryParam, search = searchParam)
            result.fold(
                onSuccess = { items ->
                    _uiState.update {
                        it.copy(
                            products = items,
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = err.message ?: "Failed to load auctions"
                        )
                    }
                }
            )
        }
    }

    fun placeQuickBid(product: Product) {
        val nextBidAmount = product.currentBid + 1000.0
        viewModelScope.launch {
            val result = repository.placeBid(product.id, nextBidAmount)
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(bidSuccessMessage = "Bid placed! New highest: ₹${nextBidAmount.toInt()}")
                    }
                    fetchProducts()
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(errorMessage = err.message ?: "Failed to place bid")
                    }
                }
            )
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, bidSuccessMessage = null) }
    }
}
