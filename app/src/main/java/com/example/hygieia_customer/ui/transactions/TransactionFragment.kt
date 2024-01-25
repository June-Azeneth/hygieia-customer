package com.example.hygieia_customer.ui.transactions

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hygieia_customer.R
import com.example.hygieia_customer.SharedViewModel
import com.example.hygieia_customer.databinding.FragmentTransactionBinding
import com.example.hygieia_customer.model.Transaction
import com.example.hygieia_customer.repository.UserRepo
import com.example.hygieia_customer.ui.dashboard.DashboardFragment
import com.example.hygieia_customer.utils.Commons

class TransactionFragment : Fragment() {
    val TAG = "TransactionFragmentMessages"
    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionList: ArrayList<Transaction>
    private val transactionViewModel: TransactionViewModel by activityViewModels()
    private val sharedViewModel : SharedViewModel by activityViewModels()
    private val userRepo = UserRepo()

    private var currentUser : String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        Commons().setToolbarIcon(R.drawable.qr_code, binding.root)

        transactionViewModel.transactionDetails.observe(viewLifecycleOwner) { transactions ->
            if (transactions != null) {
                transactionList.clear()
                transactionList.addAll(transactions)
                binding.recyclerView.adapter?.notifyDataSetChanged()
                binding.progressBar.visibility = View.GONE
            }
        }

        Commons().setOnRefreshListener(binding.refreshLayout) {
            transactionViewModel.fetchTransactions(currentUser)
        }

        setUpRecyclerView()
        return binding.root
    }

    private fun setUpRecyclerView() {
        try {
            val recyclerView = binding.recyclerView
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.setHasFixedSize(true)

            transactionList = arrayListOf()
            recyclerView.adapter = TransactionsAdapter(transactionList)

            binding.progressBar.visibility = View.VISIBLE

            currentUser = userRepo.getCurrentUserId().toString()
            sharedViewModel.fetchUserDetails(currentUser)
            sharedViewModel.userDetails.observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    transactionViewModel.fetchTransactions(user.userID)
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