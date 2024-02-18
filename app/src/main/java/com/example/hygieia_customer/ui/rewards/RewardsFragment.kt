package com.example.hygieia_customer.ui.rewards

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
import com.example.hygieia_customer.databinding.FragmentRewardsBinding
import com.example.hygieia_customer.model.Reward
import com.example.hygieia_customer.ui.promos.PromosViewModel
import com.example.hygieia_customer.utils.Commons

class RewardsFragment : Fragment(){

    val _tag = "RewardsFragmentMessages"
    private var _binding: FragmentRewardsBinding? = null
    private val binding get() = _binding!!
    private lateinit var rewardList: ArrayList<Reward>
    private val rewardViewModel: RewardsViewModel by activityViewModels()
    private val promosViewModel: PromosViewModel by activityViewModels()
    private val adapter by lazy { RewardsAdapter(arrayListOf(), onItemClickListener) }
    private val onItemClickListener = object : RewardsAdapter.OnItemClickListener {
        override fun onItemClick(item: Reward) {
            //Pass the store id extracted from the selected reward item and store it in a variable in the viewmodel
            //so that it can be used/passed to another fragment later on
            rewardViewModel.setSelectedReward(item.storeId)
            promosViewModel.setSelectedReward(item.storeId)
            findNavController().navigate(R.id.action_navigation_rewardsFragment_to_storeProfileFragment)
        }
    }
    private var commons : Commons = Commons()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRewardsBinding.inflate(inflater, container, false)

        //initialization
        binding.recyclerView.adapter = adapter

        //method calls
        setUpRecyclerView()
        commonActions()
        observeDataSetChange()
        return binding.root
    }

    private fun observeDataSetChange(){
        rewardViewModel.rewardDetails.observe(viewLifecycleOwner) { rewards ->
            if (rewards != null) {
                rewardList.clear() // Clearing and updating rewardList
                rewardList.addAll(rewards)
                adapter.setData(rewardList) // Setting data to the adapter when new rewards are received
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun commonActions(){
        commons.setToolbarIcon(R.drawable.filter, binding.root)
        commons.setOnRefreshListener(binding.refreshLayout) {
            //Fetch rewards from database
            rewardViewModel.fetchRewards()
        }
        commons.setPageTitle("Rewards", binding.root)
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
            Log.e(_tag, error.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}