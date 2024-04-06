package com.example.hygieia_customer.pages.scanQR

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hygieia_customer.model.Store
import com.example.hygieia_customer.repository.StoreRepo
import kotlinx.coroutines.launch

class StoreViewModel : ViewModel() {
    private val storeRepo: StoreRepo = StoreRepo()
    private val _storeDetails = MutableLiveData<List<Store>?>()
    val storeDetails: LiveData<List<Store>?> get() = _storeDetails
//    private val _selectedReward = MutableLiveData<String>()
//    val selectedReward: LiveData<String> get() = _selectedReward

    fun fetchStores() {
        viewModelScope.launch {
            storeRepo.getStores() { stores ->
                _storeDetails.value = stores
            }
        }
    }

    fun searchForAStore(storeName: String) {
        viewModelScope.launch {
            storeRepo.searchStore(storeName) { stores ->
                _storeDetails.value = stores
            }
        }
    }

}