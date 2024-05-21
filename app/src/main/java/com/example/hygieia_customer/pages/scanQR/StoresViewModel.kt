package com.example.hygieia_customer.pages.scanQR

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hygieia_customer.model.Store
import com.example.hygieia_customer.repository.StoreRepo
import kotlinx.coroutines.launch

class StoresViewModel : ViewModel() {
    private val storeRepo: StoreRepo = StoreRepo()
    private val _storeDetails = MutableLiveData<List<Store>?>()
    val storeDetails: LiveData<List<Store>?> get() = _storeDetails

    fun fetchStores(callback: (success: Boolean, error: String?) -> Unit) {
        viewModelScope.launch {
            storeRepo.getStores() { success, stores, error ->
                if (success) {
                    _storeDetails.value = stores
                }
                // Pass the success flag and error message to the caller
                callback(success, error)
            }
        }
    }

//    fun searchForAStore(storeName: String) {
//        viewModelScope.launch {
//            storeRepo.searchStore(storeName) { stores ->
//                _storeDetails.value = stores
//            }
//        }
//    }

}