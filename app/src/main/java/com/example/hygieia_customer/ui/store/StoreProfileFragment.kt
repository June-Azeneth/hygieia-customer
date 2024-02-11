package com.example.hygieia_customer.ui.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hygieia_customer.databinding.FragmentStoreProfileBinding
import com.example.hygieia_customer.ui.rewards.RewardsAdapter
import com.example.hygieia_customer.ui.rewards.RewardsViewModel

//TODO: implement logic for promos as well. The reward and promo items are not clickable. Build the store profile.

class StoreProfileFragment : Fragment() {
    private val rewardViewModel: RewardsViewModel by activityViewModels()
    private var _binding: FragmentStoreProfileBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazy { RewardsAdapter(arrayListOf()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoreProfileBinding.inflate(inflater, container, false)

        setUpRecyclerView()
        observeViewModel()

        return binding.root
    }

    private fun setUpRecyclerView() {
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        rewardViewModel.selectedReward.observe(viewLifecycleOwner) { storeId ->
            rewardViewModel.fetchRewardsBasedOnStoreId(storeId)
        }

        rewardViewModel.rewardDetails.observe(viewLifecycleOwner) { rewards ->
            rewards?.let {
                adapter.setData(it)
                if (it.isEmpty()) {
                    // Handle case when reward list is empty
                } else {
                    // Handle case when reward list is not empty
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}