package com.example.hygieia_customer.utils

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class NetworkViewModel(context : Context) : ViewModel() {
    private val networkManager = NetworkManager(context)
    val isNetworkAvailable: LiveData<Boolean> = networkManager
}