package com.example.hygieia_customer.pages.store

import androidx.lifecycle.LiveData
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

    private val _fromScreen = MutableLiveData<String>()
    val fromScreen : LiveData<String> get() = _fromScreen

    private val _storeId = MutableLiveData<String>()
    val storeId : LiveData<String> get() = _storeId

    fun fetchStoreDetails(storeId: String) {
        viewModelScope.launch {
            userRepo.getStoreDetails(storeId) { store ->
                _storeDetails.value = store
            }
        }
    }

    fun setAction(screen: String){
        _fromScreen.value = screen
    }

    fun setStoreId(storeId: String){
        _storeId.value = storeId
    }

    fun clearStoreDetails() {
        _storeDetails.postValue(null)
    }
}