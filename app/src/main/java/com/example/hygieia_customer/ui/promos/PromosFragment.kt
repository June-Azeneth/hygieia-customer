package com.example.hygieia_customer.ui.promos

import android.annotation.SuppressLint
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
import com.example.hygieia_customer.databinding.FragmentPromosBinding
import com.example.hygieia_customer.model.Promo
import com.example.hygieia_customer.ui.rewards.RewardsViewModel
import com.example.hygieia_customer.utils.Commons

class PromosFragment : Fragment() {

    val TAG = "PromosFragmentMessages"

    private var _binding: FragmentPromosBinding? = null
    private val binding get() = _binding!!

    private lateinit var promoList: ArrayList<Promo>
    private val rewardViewModel: RewardsViewModel by activityViewModels()
    private val promosViewModel: PromosViewModel by activityViewModels()
    private var commons : Commons = Commons()
    private val adapter by lazy { PromosAdapter(arrayListOf(), onItemClickListener)}
    private val onItemClickListener = object : PromosAdapter.OnItemClickListener {
        override fun onItemClick(item: Promo) {
            //Pass the store id extracted from the selected promo item and store it in a variable in the view model
            //so that it can be used/passed to another fragment later on
            rewardViewModel.setSelectedReward(item.storeId)
            promosViewModel.setSelectedReward(item.storeId)
            findNavController().navigate(R.id.action_navigation_promosFragment_to_storeProfileFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPromosBinding.inflate(inflater, container, false)

        //initialization
        binding.recyclerView.adapter = adapter

        //method calls
        setUpRecyclerView()
        commonActions()
        observeDataChanges()

        return binding.root
    }

    private fun commonActions(){
        commons.setToolbarIcon(R.drawable.filter, binding.root)
        commons.setOnRefreshListener(binding.refreshLayout) {
            promosViewModel.fetchPromos()
        }
        commons.setPageTitle("Promos", binding.root)
    }

    private fun observeDataChanges(){
        promosViewModel.promoDetails.observe(viewLifecycleOwner) { promos ->
            // Update UI with the new data
            if (promos != null) {
                promoList.clear()
                promoList.addAll(promos)
                adapter.setData(promoList)
                binding.progressBar.visibility = View.GONE
            }
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
            Log.e(TAG, error.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}