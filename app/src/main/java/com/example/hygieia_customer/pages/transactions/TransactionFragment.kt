package com.example.hygieia_customer.pages.transactions

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hygieia_customer.R
import com.example.hygieia_customer.databinding.FragmentTransactionBinding
import com.example.hygieia_customer.model.Transaction
import com.example.hygieia_customer.repository.UserRepo
import com.example.hygieia_customer.utils.Commons
import com.example.hygieia_customer.utils.NetworkViewModel
import com.example.hygieia_customer.utils.SharedViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TransactionFragment : Fragment() {
    private val logTag = "TransactionFragmentMessages"
    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionList: ArrayList<Transaction>
    private val transactionViewModel: TransactionViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val adapter by lazy { TransactionsAdapter(arrayListOf()) }
    private val userRepo = UserRepo()
    private var currentUser: String = ""
    private lateinit var dialog : AlertDialog
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)

        //initialization
        binding.recyclerView.adapter = adapter
        dialog = MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
            .setView(R.layout.connectivity_dialog_box)
            .setCancelable(true)
            .create()
        progressBar = binding.progressBar

        //method calls
        commonActions()
        setUpRecyclerView()
        observeDataChanges()

        return binding.root
    }

    private fun observeDataChanges() {
        val networkViewModel = NetworkViewModel(requireContext())
        networkViewModel.isNetworkAvailable.observe(viewLifecycleOwner) { available ->
            if (available) {
                binding.transactionsPlaceholder.visibility = View.INVISIBLE
                binding.actualLayout.visibility = View.VISIBLE
                transactionViewModel.transactionDetails.observe(viewLifecycleOwner) { transactions ->
                    if (transactions != null) {
                        transactionList.clear()
                        transactionList.addAll(transactions)
                        adapter.setData(transactionList)
                        progressBar.visibility = View.INVISIBLE
                        if (transactionList.isEmpty()) {
                            showNoDataMessage(true)
                        } else {
                            showNoDataMessage(false)
                        }
                    }
                }
            }
            else{
                binding.transactionsPlaceholder.visibility = View.VISIBLE
                binding.actualLayout.visibility = View.INVISIBLE
                if(!dialog.isShowing)
                    dialog.show()
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

            progressBar.visibility = View.VISIBLE

            currentUser = userRepo.getCurrentUserId().toString()
            sharedViewModel.fetchUserDetails(currentUser)
            sharedViewModel.userDetails.observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    transactionViewModel.fetchTransactions(user.id)
                }
            }
        } catch (error: Exception) {
            Log.e(logTag, error.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}