package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.CurrencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CurrencyViewModel : ViewModel() {

    private val repository = CurrencyRepository()

    private val _rates = MutableStateFlow<Map<String, Double>?>(null)
    val rates: StateFlow<Map<String, Double>?> = _rates

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchRates(baseCurrency: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = repository.getRates(baseCurrency)
                _rates.value = response.rates
            } catch (e: Exception) {
                Log.e("CurrencyViewModel", "Error fetching rates", e)
                _errorMessage.value = "Failed to fetch rates: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}