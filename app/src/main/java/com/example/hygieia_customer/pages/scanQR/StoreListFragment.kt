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
    private lateinit var actualLayout: ConstraintLayout
    private lateinit var placeholder: ShimmerFrameLayout
    private val storeList: ArrayList<Store> = arrayListOf()
    private lateinit var dialog: AlertDialog

    private val adapter by lazy { StoreListAdapter(arrayListOf(), onItemClickListener) }
    private val onItemClickListener = object : StoreListAdapter.OnItemClickListener {
        override fun onItemClick(item: Store) {
            rewardViewModel.setSelectedReward(item.storeId)
            promosViewModel.setSelectedReward(item.storeId)
            sharedViewModel.setStoreListNav("fromStoreList")
            findNavController().navigate(R.id.to_store_profile)
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

        return binding.root
    }

    private fun setUpUi() {
        networkViewModel = NetworkViewModel(requireContext())
        actualLayout = binding.actualLayout
        placeholder = binding.placeholder

        Commons().setPageTitle("Hygieia Affiliated Stores", binding.root)

        binding.searchItem.doOnTextChanged { text, _, _, _ ->
            val storeName = text.toString().trim()
            if (storeName.isEmpty()) {
                storeViewModel.fetchStores()
            } else {
                binding.progressBar.visibility = View.VISIBLE
                storeViewModel.searchForAStore(storeName)
            }
        }
    }

    private fun observeDataSetChange() {
        storeViewModel.storeDetails.observe(viewLifecycleOwner) { stores ->
            if (stores != null) {
                Commons().log("STORESLIST", stores.size.toString())
                storeList.clear()
                storeList.addAll(stores)
                adapter.setData(storeList)
                binding.progressBar.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                showNoDataMessage(false)
            } else {
                storeList.clear()
                binding.recyclerView.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                showNoDataMessage(true)
            }
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

            recyclerView.adapter = adapter

            binding.progressBar.visibility = View.VISIBLE

            storeViewModel.fetchStores()
        } catch (error: Exception) {
            Commons().showToast("An error occurred: $error", requireContext())
        }
    }

    private fun observeNetwork() {
        dialog = MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
            .setView(R.layout.connectivity_dialog_box)
            .setCancelable(true)
            .create()
        networkViewModel.isNetworkAvailable.observe(viewLifecycleOwner) { available ->
            if (available) {
                actualLayout.visibility = View.VISIBLE
                placeholder.visibility = View.INVISIBLE
                dialog.hide()
                observeDataSetChange()
            } else {
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