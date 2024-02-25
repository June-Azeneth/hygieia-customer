package com.example.hygieia_customer.pages.promos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hygieia_customer.model.Promo
import com.example.hygieia_customer.repository.PromosRepo
import kotlinx.coroutines.launch

class PromosViewModel : ViewModel() {
    private val _selectedReward = MutableLiveData<String>()
    val selectedReward: LiveData<String> get() = _selectedReward
    private val _promoDetails = MutableLiveData<List<Promo>>()
    val promoDetails: LiveData<List<Promo>> get() = _promoDetails
    private val promosRepo: PromosRepo = PromosRepo()

    fun fetchPromos() {
        viewModelScope.launch {
            promosRepo.getPromos() { promos ->
                _promoDetails.value = promos
            }
        }
    }

    fun setSelectedReward(storeId: String) {
        _selectedReward.value = storeId
    }

    fun fetchPromosBasedOnStoreId(selectedReward: String) {
        viewModelScope.launch {
            promosRepo.getPromosByStoreId(selectedReward) { promos ->
                _promoDetails.value = promos
            }
        }
    }
}