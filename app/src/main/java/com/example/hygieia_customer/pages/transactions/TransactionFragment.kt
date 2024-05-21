package com.example.hygieia_customer.pages.transactions

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hygieia_customer.R
import com.example.hygieia_customer.databinding.FragmentTransactionBinding
import com.example.hygieia_customer.model.Transaction
import com.example.hygieia_customer.pages.scanQR.StoresViewModel
import com.example.hygieia_customer.repository.UserRepo
import com.example.hygieia_customer.utils.Commons
import com.example.hygieia_customer.utils.NetworkViewModel
import com.example.hygieia_customer.utils.SharedViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Date

class TransactionFragment : Fragment() {
    private val logTag = "TransactionFragmentMessages"
    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    private var selectedStore: String = "All Stores" //default value
    private var transactionList: ArrayList<Transaction> = arrayListOf()
    private val storeList = mutableListOf("All Stores")
    private val transactionViewModel: TransactionViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val storeViewModel: StoresViewModel by activityViewModels()
    private var adapter : TransactionsAdapter = TransactionsAdapter(
        transactionList,
        object : TransactionsAdapter.OnItemClickListener {
            override fun onItemClick(item: Transaction) {
                val dialog = TransactionDetailsDialog(requireContext(), item)
                dialog.show()
            }
        }
    )
    private val userRepo = UserRepo()
    private var currentUser: String = ""
    private lateinit var dialog: AlertDialog
    private lateinit var progressBar: ProgressBar
    private var startDate: Date? = null
    private var endDate: Date? = null

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
        getAllStores()
        observeDataChanges()

        return binding.root
    }

    private fun getAllStores() {
        storeViewModel.storeDetails.observe(viewLifecycleOwner) { stores ->
            if (stores != null) {
                val storeNames = stores.map { store -> store.name }
                storeList.addAll(storeNames)
            }
        }
    }

    private fun setUpDropdown() {
        try {
            val spinner: Spinner = binding.filterByStore
            val spinnerAdapter =
                ArrayAdapter(requireContext(), R.layout.spinner_item_dark, storeList)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = spinnerAdapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedStore = storeList[position]
                    filterTransactions(selectedStore, startDate, endDate)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Do nothing here
                }
            }
        } catch (error: Exception) {
            Commons().log(logTag, error.toString())
        }
    }

    private fun setUpListener() {
        binding.filterByDate.setOnClickListener {
            showDateRangePickerDialog()
        }
    }

    private fun showDateRangePickerDialog() {
        val datePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTheme(R.style.ThemeMaterialCalendar)
            .setSelection(Pair(null, null))
            .build()

        datePicker.show(childFragmentManager, "DatePicker")
        datePicker.addOnPositiveButtonClickListener { dateSelection ->
            startDate = Date(dateSelection.first ?: 0)
            endDate = Date(dateSelection.second ?: 0)

            // Update the text of the filterByDate button to show the selected date range
            binding.filterByDate.text = getString(
                R.string.date_range_transactions,
                Commons().dateFormatMMMDDYYYY(dateSelection.first),
                Commons().dateFormatMMMDDYYYY(dateSelection.second)
            )

            // Filter transactions based on the selected store and date range
            filterTransactions(selectedStore, startDate, endDate)
        }
    }

    private fun observeDataChanges() {
        val networkViewModel = NetworkViewModel(requireContext())
        networkViewModel.isNetworkAvailable.observe(viewLifecycleOwner) { available ->
            if (available) {
                binding.transactionsPlaceholder.visibility = View.INVISIBLE
                binding.actualLayout.visibility = View.VISIBLE
            } else {
                binding.transactionsPlaceholder.visibility = View.VISIBLE
                binding.actualLayout.visibility = View.INVISIBLE
                if (!dialog.isShowing)
                    dialog.show()
            }
        }

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

                setUpListener()
                setUpDropdown()
            }
        }
    }

    private fun filterTransactions(store: String, startDate: Date?, endDate: Date?) {
        val filteredTransactions =
            if (store == "All Stores") {
                transactionList.filter { transaction ->
                    // Filter transactions based on date range if "All" is not selected
                    filterByDateRange(transaction, startDate, endDate)
                }
            } else {
                transactionList.filter { transaction ->
                    // Filter transactions based on store name and date range
                    transaction.storeName.contains(store, ignoreCase = true) &&
                            filterByDateRange(transaction, startDate, endDate)
                }
            }
        adapter.setData(filteredTransactions)
        showNoDataMessage(filteredTransactions.isEmpty())
    }

    private fun filterByDateRange(
        transaction: Transaction,
        startDate: Date?,
        endDate: Date?
    ): Boolean {
        // If either start date or end date is null, return true to include the transaction
        if (startDate == null || endDate == null) {
            return true
        }
        // If both start date and end date are provided, check if the transaction date is within the range
        return transaction.addedOn!! in startDate..endDate
    }

    private fun commonActions() {
        Commons().setOnRefreshListener(binding.refreshLayout) {
            transactionViewModel.fetchTransactions()
        }
    }

    private fun showNoDataMessage(show: Boolean) {
        if (show) {
            binding.recyclerView.visibility = View.GONE
            binding.imageMessage.setImageResource(R.drawable.no_transactions)
            binding.imageMessage.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
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
                    transactionViewModel.fetchTransactions()
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