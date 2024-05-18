package com.example.hygieia_customer.pages.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hygieia_customer.model.Transaction
import com.example.hygieia_customer.model.TransactionsFilterConfig
import com.example.hygieia_customer.repository.TransactionsRepo
import com.example.hygieia_customer.utils.Commons
import kotlinx.coroutines.launch

class TransactionViewModel : ViewModel() {
    private val _transactionDetails = MutableLiveData<List<Transaction>?>()
    val transactionDetails: LiveData<List<Transaction>?> get() = _transactionDetails
    private val transactionsRepo: TransactionsRepo = TransactionsRepo()

    private val _filterConfig = MutableLiveData<TransactionsFilterConfig>()
    val filterConfig: LiveData<TransactionsFilterConfig> get() = _filterConfig

    fun fetchTransactions() {
        viewModelScope.launch {
            val rewards = transactionsRepo.getTransactions()
            _transactionDetails.value = rewards
        }
    }

    fun updateFilterConfig(filterConfig: TransactionsFilterConfig) {
        _filterConfig.value = filterConfig
    }

}