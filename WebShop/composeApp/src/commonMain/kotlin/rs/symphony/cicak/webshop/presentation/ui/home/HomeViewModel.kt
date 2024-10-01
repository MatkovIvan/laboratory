package rs.symphony.cicak.webshop.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import rs.symphony.cicak.webshop.data.repository.CartRepository
import rs.symphony.cicak.webshop.data.repository.ProductRepository
import rs.symphony.cicak.webshop.domain.Currency
import rs.symphony.cicak.webshop.domain.Product
import rs.symphony.cicak.webshop.domain.ProductDetails
import rs.symphony.cicak.webshop.domain.ProductId
import rs.symphony.cicak.webshop.domain.getImageResource

sealed class HomeScreenState {
    object Loading : HomeScreenState()
    data class Success(val products: List<Product>) : HomeScreenState()
    data class Error(val message: String) : HomeScreenState()
}

class HomeViewModel(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _homeScreenState = MutableStateFlow<HomeScreenState>(HomeScreenState.Loading)
    val homeScreenState: StateFlow<HomeScreenState> = _homeScreenState

    fun fetchHomeProducts() {
        viewModelScope.launch {
            delay(600L)

            productRepository.getProducts().collect { products ->
                if (products.isNotEmpty()) {
                    _homeScreenState.value = HomeScreenState.Success(products)
                } else {
                    _homeScreenState.value = HomeScreenState.Error("No products found")
                }
            }
        }
    }

    fun toggleFavorite(productId: Long) {
        viewModelScope.launch {
            productRepository.toggleFavorite(productId)
        }
    }

    fun addToCart(id: Long) {
        viewModelScope.launch {
            cartRepository.addToCart(id)
        }
    }

    fun getProduct(productId: ProductId): ProductDetails {
        val thumb = productRepository.getProducts().value.first { it.id == productId }
        val ret = ProductDetails(
            id = thumb.id,
            title = thumb.title,
            subtitle = thumb.subtitle,
            price = thumb.price,
            currency = Currency.USD.symbol,
            description = "",
            images = listOf(thumb.getImageResource()),
        )
        return ret
    }
}