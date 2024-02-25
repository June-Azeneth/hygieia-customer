package com.example.hygieia_customer.pages.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hygieia_customer.R
import com.example.hygieia_customer.databinding.FragmentPromosTabBinding
import com.example.hygieia_customer.model.Promo
import com.example.hygieia_customer.pages.promos.PromosAdapter
import com.example.hygieia_customer.pages.promos.PromosViewModel

class PromosTab : Fragment() {
    private val promosViewModel: PromosViewModel by activityViewModels()
    private var _binding: FragmentPromosTabBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazy { PromosAdapter(arrayListOf()) }
    private lateinit var promoList: ArrayList<Promo>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPromosTabBinding.inflate(inflater, container, false)

        //Method Calls
        setUpRecyclerView()
        observeDataSetChange()

        return binding.root
    }

    private fun observeDataSetChange() {
        promosViewModel.selectedReward.observe(viewLifecycleOwner) { storeId ->
            promosViewModel.fetchPromosBasedOnStoreId(storeId)
        }

        promosViewModel.promoDetails.observe(viewLifecycleOwner) { promo ->
            if (promo != null) {
                promoList.clear() // Clearing and updating rewardList
                promoList.addAll(promo)
                adapter.setData(promoList) // Setting data to the adapter when new rewards are received
                binding.progressBar.visibility = View.GONE
                if (promoList.isEmpty()) {
                    showNoDataMessage(true)
                } else {
                    showNoDataMessage(false)
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        promoList = arrayListOf()
        recyclerView.adapter = adapter
    }

    private fun showNoDataMessage(show: Boolean) {
        if (show) {
            binding.imageMessage.setImageResource(R.drawable.no_data)
            binding.imageMessage.visibility = View.VISIBLE
            binding.message.visibility = View.VISIBLE
        } else {
            binding.imageMessage.visibility = View.GONE
            binding.message.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}