package com.example.hygieia_customer.pages.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hygieia_customer.model.Ads
import com.example.hygieia_customer.repository.AdsRepo
import kotlinx.coroutines.launch

class AdsViewModel : ViewModel() {
    private val adsRepo: AdsRepo = AdsRepo()
    private val _adsList = MutableLiveData<List<Ads>?>()
    val adsList: MutableLiveData<List<Ads>?> get() = _adsList

    fun fetchAllRewards(){
        viewModelScope.launch {
            val rewards = adsRepo.getAllAds {ads ->
                if(ads != null){
                    _adsList.value = ads
                }
            }
        }
    }
}