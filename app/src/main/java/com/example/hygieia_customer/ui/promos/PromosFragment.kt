package com.example.hygieia_customer.ui.promos

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hygieia_customer.R
import com.example.hygieia_customer.databinding.FragmentPromosBinding
import com.example.hygieia_customer.model.Promo
import com.example.hygieia_customer.utils.Commons

class PromosFragment : Fragment() {

    val TAG = "PromosFragmentMessages"

    private var _binding: FragmentPromosBinding? = null
    private val binding get() = _binding!!

    private lateinit var promoList: ArrayList<Promo>
    private val promosViewModel: PromosViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPromosBinding.inflate(inflater, container, false)

        Commons().setToolbarIcon(R.drawable.filter, binding.root)
        Commons().setOnRefreshListener(binding.refreshLayout) {
            promosViewModel.fetchPromos()
        }

        promosViewModel.promoDetails.observe(viewLifecycleOwner) { promos ->
            // Update UI with the new data
            if (promos != null) {
                promoList.clear()
                promoList.addAll(promos)
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
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.setHasFixedSize(true)

            promoList = arrayListOf()
            recyclerView.adapter = PromoAdapter(promoList)

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