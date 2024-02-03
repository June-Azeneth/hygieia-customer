package com.example.hygieia_customer.ui.rewards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hygieia_customer.model.Promo
import com.example.hygieia_customer.model.Reward
import com.example.hygieia_customer.model.UserInfo
import com.example.hygieia_customer.repository.PromosRepo
import com.example.hygieia_customer.repository.RewardRepo
import com.example.hygieia_customer.repository.RewardsRepo
import com.example.hygieia_customer.repository.UserRepo
import kotlinx.coroutines.launch

class RewardsViewModel : ViewModel() {
    private val _rewardDetails = MutableLiveData<List<Reward>>()
    val rewardDetails: LiveData<List<Reward>> get() = _rewardDetails
    private val rewardRepo: RewardsRepo = RewardsRepo()

    private val rewardsRepo : RewardRepo= RewardRepo()

    fun fetchRewards() {
        viewModelScope.launch {
            rewardRepo.getRewards () { reward ->
                _rewardDetails.value = reward
            }
        }
    }

    fun fetchAllRewards(){
        viewModelScope.launch {
            rewardsRepo.getRewards () { reward ->
                _rewardDetails.value = reward
            }
        }
    }
}