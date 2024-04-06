package com.example.hygieia_customer.pages.promos

import android.os.Bundle
import android.util.Log
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
import com.example.hygieia_customer.databinding.FragmentPromosBinding
import com.example.hygieia_customer.model.Promo
import com.example.hygieia_customer.pages.rewards.RewardDetailsDialog
import com.example.hygieia_customer.pages.rewards.RewardsViewModel
import com.example.hygieia_customer.utils.Commons
import com.example.hygieia_customer.utils.NetworkViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PromosFragment : Fragment() {
    private val logTag = "PromosFragmentMessages"
    private var _binding: FragmentPromosBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialog: AlertDialog
    private lateinit var promoList: ArrayList<Promo>
    private val rewardViewModel: RewardsViewModel by activityViewModels()
    private val promosViewModel: PromosViewModel by activityViewModels()
    private lateinit var networkViewModel: NetworkViewModel
    private val adapter by lazy { PromosAdapter(arrayListOf(), onItemClickListener) }
    private val onItemClickListener = object : PromosAdapter.OnItemClickListener {
        override fun onItemClick(item: Promo) {
            //Pass the store id extracted from the selected promo item and store it in a variable in the view model
            //so that it can be used/passed to another fragment later on
            rewardViewModel.setSelectedReward(item.storeId)
            promosViewModel.setSelectedReward(item.storeId)
            val dialog = PromoDetailsDialog(requireContext(), item, findNavController())
            dialog.show()
        }
    }
    private lateinit var actualLayout: ConstraintLayout
    private lateinit var placeholder: ShimmerFrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPromosBinding.inflate(inflater, container, false)

        //method calls
        initializeVariables()
        setUpRecyclerView()
        observeNetwork()
        setUpSearch()

        return binding.root
    }
    private fun initializeVariables(){
        binding.recyclerView.adapter = adapter
        networkViewModel = NetworkViewModel(requireContext())
        actualLayout = binding.actualLayout
        placeholder = binding.placeholder
    }


    private fun setUpSearch(){
        binding.searchItem.doOnTextChanged { text, _, _, _ ->
            val storeName = text.toString().trim()
            if (storeName.isEmpty()) {
                promosViewModel.fetchPromos()
            } else {
                binding.progressBar.visibility = View.VISIBLE
                promosViewModel.searchPromo(storeName)
            }
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
                observeDataChanges()
            } else {
                actualLayout.visibility = View.INVISIBLE
                placeholder.visibility = View.VISIBLE
                promoList.clear()
                if (!dialog.isShowing)
                    dialog.show()
            }
        }
    }

    private fun observeDataChanges() {
        promosViewModel.promoDetails.observe(viewLifecycleOwner) { promos ->
            // Update UI with the new data
            if (promos != null) {
                promoList.clear()
                promoList.addAll(promos)
                adapter.setData(promoList)
                binding.progressBar.visibility = View.GONE
                if (promoList.isEmpty()) {
                    showNoDataMessage(true)
                } else {
                    showNoDataMessage(false)
                }
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

            promoList = arrayListOf()
            recyclerView.adapter = adapter

            binding.progressBar.visibility = View.VISIBLE

            promosViewModel.fetchPromos()
        } catch (error: Exception) {
            Log.e(logTag, error.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}