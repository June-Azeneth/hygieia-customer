package com.example.hygieia_customer.pages.rewards

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hygieia_customer.R
import com.example.hygieia_customer.databinding.FragmentRewardsBinding
import com.example.hygieia_customer.model.Reward
import com.example.hygieia_customer.pages.promos.PromosViewModel
import com.example.hygieia_customer.utils.NetworkViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RewardsFragment : Fragment() {

    val logTag = "RewardsFragmentMessages"
    private var _binding: FragmentRewardsBinding? = null
    private val binding get() = _binding!!
    private lateinit var rewardList: ArrayList<Reward>
    private lateinit var dialog: AlertDialog
    private val rewardViewModel: RewardsViewModel by activityViewModels()
    private val promosViewModel: PromosViewModel by activityViewModels()
    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var actualLayout : ConstraintLayout
    private lateinit var placeholder : ShimmerFrameLayout

    private val adapter by lazy { RewardsAdapter(arrayListOf(), onItemClickListener) }
    private val onItemClickListener = object : RewardsAdapter.OnItemClickListener {
        override fun onItemClick(item: Reward) {
            //Pass the store id extracted from the selected reward item and store it in a variable in the viewmodel
            //so that it can be used/passed to another fragment later on
            rewardViewModel.setSelectedReward(item.storeId)
            promosViewModel.setSelectedReward(item.storeId)
            val dialog = RewardDetailsDialog(requireContext(), item, findNavController())
            dialog.show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRewardsBinding.inflate(inflater, container, false)

        //initialization
        binding.recyclerView.adapter = adapter
        networkViewModel = NetworkViewModel(requireContext())
        actualLayout = binding.actualLayout
        placeholder = binding.placeholder

        //method calls
        setUpRecyclerView()
        commonActions()
        observeNetwork()
        return binding.root
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
                rewardList.clear()
                if (!dialog.isShowing)
                    dialog.show()
            }
        }
    }

    private fun observeDataSetChange() {
        rewardViewModel.rewardDetails.observe(viewLifecycleOwner) { rewards ->
            if (rewards != null) {
                rewardList.clear()
                rewardList.addAll(rewards)
                adapter.setData(rewardList)
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun commonActions() {
//        commons.setOnRefreshListener(binding.refreshLayout) {
//            observeNetwork()
//        }
//        observeNetwork()
    }

    private fun setUpRecyclerView() {
        try {
            val recyclerView = binding.recyclerView

            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.setHasFixedSize(true)

            rewardList = arrayListOf()

            recyclerView.adapter = adapter

            binding.progressBar.visibility = View.VISIBLE

            rewardViewModel.fetchRewards()
        } catch (error: Exception) {
            Log.e(logTag, error.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}