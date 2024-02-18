package com.example.hygieia_customer.ui.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hygieia_customer.R
import com.example.hygieia_customer.databinding.FragmentRewardsTabBinding
import com.example.hygieia_customer.model.Reward
import com.example.hygieia_customer.ui.rewards.RewardsAdapter
import com.example.hygieia_customer.ui.rewards.RewardsViewModel
import com.example.hygieia_customer.utils.Commons

class RewardsTab : Fragment() {

    private val rewardViewModel: RewardsViewModel by activityViewModels()
    private var _binding: FragmentRewardsTabBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazy { RewardsAdapter(arrayListOf(), onItemClickListener) }
    private lateinit var rewardList: ArrayList<Reward>

    private val onItemClickListener = object : RewardsAdapter.OnItemClickListener {
        override fun onItemClick(item: Reward) {
            //DO NOTHING
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRewardsTabBinding.inflate(inflater, container, false)
        //Method Calls
        setUpRecyclerView()
        observeDataSetChange()
        return binding.root
    }

    private fun observeDataSetChange() {
        rewardViewModel.selectedReward.observe(viewLifecycleOwner) { storeId ->
            rewardViewModel.fetchRewardsBasedOnStoreId(storeId)
        }

        rewardViewModel.storeProfileRewards.observe(viewLifecycleOwner) { rewards ->
            if (rewards != null) {
                rewardList.clear() // Clearing and updating rewardList
                rewardList.addAll(rewards)
                adapter.setData(rewardList) // Setting data to the adapter when new rewards are received
                binding.progressBar.visibility = View.GONE
                if (rewardList.isEmpty()) {
                    showNoDataMessage(true)
                } else {
                    showNoDataMessage(false)
                }
            }
        }
    }

    private fun showNoDataMessage(show: Boolean) {
        binding.progressBar.visibility = View.GONE
        if (show) {
            binding.imageMessage.setImageResource(R.drawable.no_data)
            binding.imageMessage.visibility = View.VISIBLE
            binding.message.visibility = View.VISIBLE
        } else {
            binding.imageMessage.visibility = View.GONE
            binding.message.visibility = View.GONE
        }
    }

    private fun setUpRecyclerView() {
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        rewardList = arrayListOf()
        recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}