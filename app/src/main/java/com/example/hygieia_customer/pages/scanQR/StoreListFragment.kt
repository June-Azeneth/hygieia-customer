package com.example.hygieia_customer.pages.scanQR

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.hygieia_customer.R
import com.example.hygieia_customer.databinding.FragmentStoreListBinding
import com.example.hygieia_customer.model.Store
import com.example.hygieia_customer.pages.promos.PromosViewModel
import com.example.hygieia_customer.pages.rewards.RewardsViewModel
import com.example.hygieia_customer.utils.Commons
import com.example.hygieia_customer.utils.NetworkViewModel
import com.example.hygieia_customer.utils.SharedViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class StoreListFragment : Fragment() {

    private var _binding: FragmentStoreListBinding? = null
    private val binding get() = _binding!!
    private val rewardViewModel: RewardsViewModel by activityViewModels()
    private val promosViewModel: PromosViewModel by activityViewModels()
    private val storeViewModel: StoreViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var actualLayout: SwipeRefreshLayout
    private lateinit var placeholder: ShimmerFrameLayout
    private val storeList: ArrayList<Store> = arrayListOf()
    private lateinit var dialog: AlertDialog
    private var internetAvailable : Boolean = true

    private val adapter by lazy { StoreListAdapter(arrayListOf(), onItemClickListener) }
    private val onItemClickListener = object : StoreListAdapter.OnItemClickListener {
        override fun onItemClick(item: Store) {
            rewardViewModel.setSelectedReward(item.storeId)
            promosViewModel.setSelectedReward(item.storeId)
            sharedViewModel.setStoreListNav("fromStoreList")
            findNavController().navigate(R.id.action_storeListFragment2_to_storeProfileFragment2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStoreListBinding.inflate(inflater, container, false)

        setUpUi()
        observeDataSetChange()
        setUpRecyclerView()
        observeNetwork()
        elasticSearch()

        return binding.root
    }

    private fun setUpUi() {
        networkViewModel = NetworkViewModel(requireContext())
        actualLayout = binding.actualLayout
        placeholder = binding.placeholder

        Commons().setOnRefreshListener(actualLayout){
            showNoDataMessage(false)
            loadList(true)
            storeViewModel.fetchStores{ success, error ->
                loadList(false)
                if (!success) {
                    Commons().showToast("Failed to fetch stores: $error", requireContext())
                }
            }
        }
    }

    private fun elasticSearch() {
        binding.searchItem.doOnTextChanged { text, _, _, _ ->
            if(internetAvailable){
                dialog.hide()

                binding.progressBar.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE

                val searchText = text.toString().trim()
                val filteredList = if (searchText.isEmpty()) {
                    // If search text is empty, show all stores
                    storeList
                } else {
                    // Filter stores based on search text
                    storeList.filter { store ->
                        // You can customize the search criteria here, for example, by store name
                        store.name.contains(searchText, ignoreCase = true) || store.address.contains(searchText, ignoreCase = true)
                    }
                }
                // Update the adapter with the filtered list
                adapter.setData(filteredList)

                if(filteredList.isEmpty()){
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    showNoDataMessage(true)
                }
                else{
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    showNoDataMessage(false)
                }
            }
            else{
                if (!dialog.isShowing)
                    dialog.show()
            }
        }
    }

    private fun observeDataSetChange() {
        storeViewModel.storeDetails.observe(viewLifecycleOwner) { stores ->
            if (stores != null) {
                storeList.clear()
                storeList.addAll(stores)
                adapter.setData(storeList)
                showNoDataMessage(false)
            }
        }
    }

    private fun showNoDataMessage(show: Boolean) {
        if (show) {
            binding.imageMessage.setImageResource(R.drawable.no_result)
            binding.imageMessage.visibility = View.VISIBLE
        } else {
            binding.imageMessage.visibility = View.GONE
        }
    }

    private fun setUpRecyclerView() {
        try {
            loadList(true)
            val recyclerView = binding.recyclerView
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter

            storeViewModel.fetchStores{ success, error ->
                loadList(false)
                if (success) {
                    observeDataSetChange()
                }
                else{
                    Commons().showToast("Failed to fetch stores: $error", requireContext())
                }
            }
        } catch (error: Exception) {
            Commons().showToast("An error occurred: $error", requireContext())
        }
    }

    fun loadList(load: Boolean){
        if(load){
            binding.progressBar.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        }
        else{
            binding.progressBar.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    private fun observeNetwork() {
        dialog = MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
            .setView(R.layout.connectivity_dialog_box)
            .setCancelable(true)
            .create()
        networkViewModel.isNetworkAvailable.observe(viewLifecycleOwner) { available ->
            if (available) {
                internetAvailable = true
                actualLayout.visibility = View.VISIBLE
                placeholder.visibility = View.INVISIBLE
                dialog.hide()
                observeDataSetChange()
            } else {
                internetAvailable = false
                actualLayout.visibility = View.INVISIBLE
                placeholder.visibility = View.VISIBLE
                storeList.clear()
                if (!dialog.isShowing)
                    dialog.show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}