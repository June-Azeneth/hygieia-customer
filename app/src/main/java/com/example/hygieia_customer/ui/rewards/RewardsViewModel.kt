package com.example.hygieia_customer.ui.rewards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hygieia_customer.model.Reward
import com.example.hygieia_customer.repository.RewardsRepo
import kotlinx.coroutines.launch

class RewardsViewModel : ViewModel() {
    private val _rewardDetails = MutableLiveData<List<Reward>>()
    val rewardDetails: LiveData<List<Reward>> get() = _rewardDetails
    private val rewardsRepo : RewardsRepo = RewardsRepo()

    fun fetchRewards() {
        viewModelScope.launch {
            rewardsRepo.getRewards { rewards ->
                _rewardDetails.value = rewards
            }
        }
    }
}