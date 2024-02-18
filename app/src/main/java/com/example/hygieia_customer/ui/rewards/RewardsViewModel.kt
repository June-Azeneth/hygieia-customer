package com.example.hygieia_customer.ui.rewards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hygieia_customer.model.Reward
import com.example.hygieia_customer.repository.RewardsRepo
import kotlinx.coroutines.launch

class RewardsViewModel : ViewModel() {
    private val rewardsRepo : RewardsRepo = RewardsRepo()
    private val _storeProfileRewards = MutableLiveData<List<Reward>?>()
    val storeProfileRewards: MutableLiveData<List<Reward>?> get() = _storeProfileRewards
    private val _rewardDetails = MutableLiveData<List<Reward>>()
    val rewardDetails: LiveData<List<Reward>> get() = _rewardDetails
    private val _selectedReward = MutableLiveData<String>()
    val selectedReward: LiveData<String> get() = _selectedReward

    //Used in: Rewards Fragment
    fun fetchRewards() {
        viewModelScope.launch {
            rewardsRepo.getRewards { rewards ->
                _rewardDetails.value = rewards
            }
        }
    }

    fun setSelectedReward(storeId: String) {
        _selectedReward.value = storeId
    }

    fun clearRewards(){
        _storeProfileRewards.postValue(null)
    }

    //Used in Store Profile Fragment
    fun fetchRewardsBasedOnStoreId(selectedReward : String){
        viewModelScope.launch {
            rewardsRepo.getRewardByStoreId(selectedReward) { rewards ->
                _storeProfileRewards.value = rewards
            }
        }
    }
}