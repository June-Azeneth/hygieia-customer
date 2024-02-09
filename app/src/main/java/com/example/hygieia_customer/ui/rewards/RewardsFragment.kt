package com.example.hygieia_customer.ui.rewards

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.hygieia_customer.R
import com.example.hygieia_customer.databinding.FragmentRewardsBinding
import com.example.hygieia_customer.model.Reward
import com.example.hygieia_customer.utils.Commons

class RewardsFragment : Fragment() {
    val TAG = "RewardsFragmentMessages"
    private var _binding: FragmentRewardsBinding? = null
    private val binding get() = _binding!!

    private lateinit var rewardList: ArrayList<Reward>
    private val rewardViewModel: RewardsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRewardsBinding.inflate(inflater, container, false)
        Commons().setToolbarIcon(R.drawable.filter, binding.root)
        Commons().setOnRefreshListener(binding.refreshLayout) {
            rewardViewModel.fetchRewards()
        }

        rewardViewModel.rewardDetails.observe(viewLifecycleOwner) { rewards ->
            if (rewards != null) {
                rewardList.clear()
                rewardList.addAll(rewards)
                binding.recyclerView.adapter?.notifyDataSetChanged()
                binding.progressBar.visibility = View.GONE
            }
        }

        setUpRecyclerView()
        return binding.root
    }

    private fun setUpRecyclerView() {
        try {
            val recyclerView = binding.recyclerView

            recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
            recyclerView.setHasFixedSize(true)

            rewardList = arrayListOf()
            recyclerView.adapter = RewardsAdapter(rewardList)

            binding.progressBar.visibility = View.VISIBLE

            rewardViewModel.fetchRewards()
        } catch (error: Exception) {
            Log.e(TAG, error.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}