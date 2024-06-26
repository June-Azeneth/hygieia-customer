package com.example.hygieia_customer.utils

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hygieia_customer.model.UserInfo
import com.example.hygieia_customer.repository.UserRepo
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {
    private val _userDetails = MutableLiveData<UserInfo>()
    val userDetails: LiveData<UserInfo> get() = _userDetails

    private val _selectedProfilePicture = MutableLiveData<String>()
    val selectedProfilePicture: LiveData<String> get() = _selectedProfilePicture

    private val userRepo: UserRepo = UserRepo()

    private val _action = MutableLiveData<String>()
    val action : LiveData<String> get() = _action

    private val _storeListNav = MutableLiveData<String>()
//    val storeListNav : LiveData<String> get() = _storeListNav

    fun fetchUserDetails(userId: String) {
        viewModelScope.launch {
            userRepo.getUserDetails(userId) { user ->
                _userDetails.value = user
            }
        }
    }

    fun setSelectedProfilePicture(imgUrl: String) {
        _selectedProfilePicture.value = imgUrl
    }

    fun setAction(action: String){
        _action.value = action
    }

    fun setStoreListNav(nav: String){
        _storeListNav.value = nav
    }
}