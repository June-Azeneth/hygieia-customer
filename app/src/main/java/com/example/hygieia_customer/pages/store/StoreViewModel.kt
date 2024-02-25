package com.example.hygieia_customer.pages.store

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hygieia_customer.model.Store
import com.example.hygieia_customer.repository.UserRepo
import kotlinx.coroutines.launch

class StoreViewModel : ViewModel() {
    private val userRepo: UserRepo = UserRepo()
    private val _storeDetails = MutableLiveData<Store?>()
    val storeDetails: MutableLiveData<Store?> get() = _storeDetails

    fun fetchStoreDetails(storeId: String) {
        viewModelScope.launch {
            userRepo.getStoreDetails(storeId) { store ->
                _storeDetails.value = store
            }
        }
    }

    fun clearStoreDetails() {
        _storeDetails.postValue(null)
    }
}