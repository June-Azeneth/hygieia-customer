package com.example.hygieia_customer.ui.transactions

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hygieia_customer.R
import com.example.hygieia_customer.SharedViewModel
import com.example.hygieia_customer.databinding.FragmentTransactionBinding
import com.example.hygieia_customer.model.Transaction
import com.example.hygieia_customer.repository.UserRepo
import com.example.hygieia_customer.utils.Commons

class TransactionFragment : Fragment() {
    val TAG = "TransactionFragmentMessages"
    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionList: ArrayList<Transaction>
    private val transactionViewModel: TransactionViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val adapter by lazy { TransactionsAdapter(arrayListOf()) }
    private val userRepo = UserRepo()
    private var currentUser: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)

        //instantiation
        binding.recyclerView.adapter = adapter

        //method calls
        commonActions()
        setUpRecyclerView()
        observeDataChanges()

        return binding.root
    }

    private fun observeDataChanges() {
        transactionViewModel.transactionDetails.observe(viewLifecycleOwner) { transactions ->
            if (transactions != null) {
                transactionList.clear()
                transactionList.addAll(transactions)
                adapter.setData(transactionList)
                binding.progressBar.visibility = View.GONE
                if (transactionList.isEmpty()) {
                    showNoDataMessage(true)
                } else {
                    showNoDataMessage(false)
                }
            }
        }
    }

    private fun commonActions() {
        Commons().setOnRefreshListener(binding.refreshLayout) {
            transactionViewModel.fetchTransactions(currentUser)
        }
        Commons().setToolbarIcon(R.drawable.qr_code, binding.root)
        Commons().setToolBarIconAction(binding.root) {
            findNavController().navigate(R.id.action_navigation_transaction_to_navigation_scanQR)
        }
    }

    private fun showNoDataMessage(show: Boolean) {
        if (show) {
            binding.imageMessage.setImageResource(R.drawable.no_data)
            binding.imageMessage.visibility = View.VISIBLE
        } else {
            binding.imageMessage.visibility = View.GONE
        }
    }

    private fun setUpRecyclerView() {
        try {
            val recyclerView = binding.recyclerView
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.setHasFixedSize(true)

            transactionList = arrayListOf()
            recyclerView.adapter = adapter

            binding.progressBar.visibility = View.VISIBLE

            currentUser = userRepo.getCurrentUserId().toString()
            sharedViewModel.fetchUserDetails(currentUser)
            sharedViewModel.userDetails.observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    transactionViewModel.fetchTransactions(user.id)
                }
            }
        } catch (error: Exception) {
            Log.e(TAG, error.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}