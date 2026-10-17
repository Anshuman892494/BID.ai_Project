package com.bidai.app.ui.screens.sell

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bidai.app.data.model.CreateProductRequest
import com.bidai.app.data.model.Product
import com.bidai.app.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class SellUiState {
    object Idle : SellUiState()
    object AnalyzingAI : SellUiState()
    object Submitting : SellUiState()
    data class Success(val product: Product) : SellUiState()
    data class Error(val message: String) : SellUiState()
}

data class SellFormState(
    val aiQuery: String = "iPhone 14 Pro 128GB",
    val title: String = "",
    val categoryId: Int = 2, // Mobiles
    val startingPrice: String = "",
    val condition: String = "Like New",
    val description: String = "",
    val imageUrl: String = "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=600&q=80",
    val estimatedEcoScore: Int = 75,
    val durationHours: Int = 24,
    val isAiFilled: Boolean = false,
    val titleError: String? = null,
    val priceError: String? = null
)

class SellViewModel(private val context: Context) : ViewModel() {
    private val repository = ProductRepository(context)

    private val _uiState = MutableStateFlow<SellUiState>(SellUiState.Idle)
    val uiState: StateFlow<SellUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(SellFormState())
    val formState: StateFlow<SellFormState> = _formState.asStateFlow()

    fun onAiQueryChange(value: String) {
        _formState.update { it.copy(aiQuery = value) }
    }

    fun onTitleChange(value: String) {
        _formState.update { it.copy(title = value, titleError = null) }
    }

    fun onPriceChange(value: String) {
        _formState.update { it.copy(startingPrice = value, priceError = null) }
    }

    fun onConditionChange(value: String) {
        _formState.update { it.copy(condition = value) }
    }

    fun onCategoryChange(categoryId: Int) {
        _formState.update { it.copy(categoryId = categoryId) }
    }

    fun onDescriptionChange(value: String) {
        _formState.update { it.copy(description = value) }
    }

    fun onImageUrlChange(url: String) {
        _formState.update { it.copy(imageUrl = url) }
    }

    // ⭐ Magic AI Auto-Fill Function
    fun triggerAiAutoFill() {
        val query = _formState.value.aiQuery
        if (query.isBlank()) return

        _uiState.value = SellUiState.AnalyzingAI

        viewModelScope.launch {
            val result = repository.getAISuggestion(
                query = query,
                condition = _formState.value.condition,
                category = _formState.value.categoryId.toString()
            )

            result.fold(
                onSuccess = { suggestion ->
                    _formState.update {
                        it.copy(
                            title = suggestion.suggestedTitle,
                            categoryId = suggestion.suggestedCategoryId,
                            startingPrice = suggestion.suggestedStartingPrice.toInt().toString(),
                            estimatedEcoScore = suggestion.estimatedEcoScore,
                            description = suggestion.generatedDescription,
                            imageUrl = suggestion.sampleImageUrl ?: it.imageUrl,
                            isAiFilled = true,
                            titleError = null,
                            priceError = null
                        )
                    }
                    _uiState.value = SellUiState.Idle
                },
                onFailure = { err ->
                    _uiState.value = SellUiState.Error(err.message ?: "Failed to generate AI auto-fill")
                }
            )
        }
    }

    fun submitListing(onSuccess: (Product) -> Unit) {
        val form = _formState.value
        var hasError = false

        if (form.title.isBlank()) {
            _formState.update { it.copy(titleError = "Product title is required") }
            hasError = true
        }

        val price = form.startingPrice.toDoubleOrNull()
        if (price == null || price <= 0) {
            _formState.update { it.copy(priceError = "Enter a valid starting bid price") }
            hasError = true
        }

        if (hasError) return

        _uiState.value = SellUiState.Submitting

        viewModelScope.launch {
            val request = CreateProductRequest(
                title = form.title,
                categoryId = form.categoryId,
                startingPrice = price!!,
                condition = form.condition,
                imageUrl = form.imageUrl,
                description = form.description,
                estimatedEcoScore = form.estimatedEcoScore,
                durationHours = form.durationHours
            )

            val result = repository.createProduct(request)
            result.fold(
                onSuccess = { product ->
                    _uiState.value = SellUiState.Success(product)
                    onSuccess(product)
                },
                onFailure = { err ->
                    _uiState.value = SellUiState.Error(err.message ?: "Failed to publish listing")
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = SellUiState.Idle
    }
}
