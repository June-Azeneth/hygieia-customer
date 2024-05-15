package com.example.hygieia_customer.pages.offers

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hygieia_customer.R
import com.example.hygieia_customer.databinding.FragmentOffersBinding
import com.example.hygieia_customer.model.Promo
import com.example.hygieia_customer.model.Reward
import com.example.hygieia_customer.pages.promos.PromoDetailsDialog
import com.example.hygieia_customer.pages.promos.PromosAdapter
import com.example.hygieia_customer.pages.promos.PromosViewModel
import com.example.hygieia_customer.pages.rewards.RewardDetailsDialog
import com.example.hygieia_customer.pages.rewards.RewardsAdapter
import com.example.hygieia_customer.pages.rewards.RewardsViewModel
import com.example.hygieia_customer.pages.store.StoreViewModel
import com.example.hygieia_customer.utils.Commons
import com.example.hygieia_customer.utils.NetworkViewModel
import com.example.hygieia_customer.utils.SharedViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class OffersFragment : Fragment() {
    private val logTag = "Offers Fragment"
    private var _binding: FragmentOffersBinding? = null
    private val binding get() = _binding!!
    private var commons: Commons = Commons()
    private val rewardViewModel: RewardsViewModel by activityViewModels()
    private val promosViewModel: PromosViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val storeViewModel: StoreViewModel by activityViewModels()
    private lateinit var reward: AppCompatButton
    private lateinit var promo: AppCompatButton
    private var currentTab: String = "rewards"
    private lateinit var dialog: AlertDialog

    private lateinit var rewardList: ArrayList<Reward>
    private lateinit var promoList: ArrayList<Promo>
    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var actualLayout: ConstraintLayout
    private lateinit var placeholder: ShimmerFrameLayout
    private val rewardsAdapter by lazy { RewardsAdapter(arrayListOf(), rewardsOnItemClickListener) }
    private val rewardsOnItemClickListener = object : RewardsAdapter.OnItemClickListener {

        override fun onItemClick(item: Reward) {
            rewardViewModel.setSelectedReward(item.storeId)
            promosViewModel.setSelectedReward(item.storeId)
            sharedViewModel.setStoreListNav("fromOffers")
            storeViewModel.setAction("offers")
            val dialog = RewardDetailsDialog(requireContext(), item, findNavController())
            dialog.show()
        }
    }

    private val promosAdapter by lazy { PromosAdapter(arrayListOf(), promosOnItemClickListener) }
    private val promosOnItemClickListener = object : PromosAdapter.OnItemClickListener {
        override fun onItemClick(item: Promo) {
            //Pass the store id extracted from the selected promo item and store it in a variable in the view model
            //so that it can be used/passed to another fragment later on
            rewardViewModel.setSelectedReward(item.storeId)
            promosViewModel.setSelectedReward(item.storeId)
            sharedViewModel.setStoreListNav("fromOffers")
            storeViewModel.setAction("offers")
            val dialog = PromoDetailsDialog(requireContext(), item, findNavController())
            dialog.show()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOffersBinding.inflate(inflater, container, false)

        initializeVariables()
        observeNetwork()
        commonActions()
        navigation()
        setUpRecyclerView()

        setUpSearch()
        return binding.root
    }

    private fun initializeVariables() {
        networkViewModel = NetworkViewModel(requireContext())
        actualLayout = binding.actualLayout
        placeholder = binding.placeholder
        reward = binding.reward
        promo = binding.promo
        binding.rewardsList.adapter = rewardsAdapter
        binding.promosList.adapter = promosAdapter
    }

    private fun commonActions() {
        commons.setOnRefreshListener(binding.swipeRefreshLayout) {
            rewardViewModel.fetchRewards()
            promosViewModel.fetchPromos()
            binding.searchItem.text?.clear()
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
                rewardList.clear()
                if (!dialog.isShowing)
                    dialog.show()
            }
        }
    }

    private fun observeDataSetChange() {
        binding.progressBar.visibility = View.VISIBLE
        binding.listContainer.visibility = View.INVISIBLE
        rewardViewModel.rewardDetails.observe(viewLifecycleOwner) { rewards ->
            if (rewards != null) {
                rewardList.clear()
                rewardList.addAll(rewards)
                rewardsAdapter.setData(rewardList)
                binding.progressBar.visibility = View.GONE
                if (rewardList.isEmpty()) {
                    showNoDataMessage(true)
                } else {
                    showNoDataMessage(false)
                }
            }
        }

        promosViewModel.promoDetails.observe(viewLifecycleOwner) { promos ->
            if (promos != null) {
                promoList.clear()
                promoList.addAll(promos)
                promosAdapter.setData(promoList)
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
            binding.listContainer.visibility = View.INVISIBLE
        } else {
            binding.imageMessage.visibility = View.GONE
            binding.listContainer.visibility = View.VISIBLE
        }
    }

    private fun navigation() {
        sharedViewModel.action.observe(viewLifecycleOwner) { action ->
            currentTab = action
            if (action == "rewards") {
                switchTabs("rewards")
                setColor(reward)
            } else {
                switchTabs("promos")
                setColor(promo)
            }
        }

        reward.setOnClickListener {
            currentTab = "rewards"
            switchTabs(currentTab)
            setColor(reward)
        }
        promo.setOnClickListener {
            currentTab = "promos"
            switchTabs(currentTab)
            setColor(promo)
        }
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchItem.windowToken, 0)
    }

    private fun switchTabs(tab: String) {
        showNoDataMessage(false)
        binding.searchItem.text?.clear()
        hideKeyboard()
        if (tab == "rewards") {
            binding.rewardsList.visibility = View.VISIBLE
            binding.promosList.visibility = View.GONE
        } else {
            binding.promosList.visibility = View.VISIBLE
            binding.rewardsList.visibility = View.GONE
        }
    }

    private fun setColor(option: AppCompatButton) {
        option.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.accent_orange))
        if (option.id == R.id.reward) {
            promo.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
        } else {
            reward.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
        }
    }

    private fun switchTabsManager(fragmentClass: Class<*>) {
//        val fragmentManager = childFragmentManager
//        val transaction = fragmentManager.beginTransaction()
//
//        // Instantiate the fragment using reflection
//        val fragment = fragmentClass.getDeclaredConstructor().newInstance() as Fragment
//
//        transaction.replace(R.id.offers_fragment_container, fragment)
//        transaction.addToBackStack(null)
//        transaction.commit()
    }

    private fun setUpSearch() {
        binding.searchBtn.setOnClickListener {
            val searchInput = binding.searchItem.text.toString().trim()
            if (searchInput.isEmpty()) {
//                if (currentTab == "rewards") {
//                    rewardViewModel.fetchRewards()
//                } else {
//                    promosViewModel.fetchPromos()
//                }
                Commons().showToast("Enter an item to search", requireContext())
            } else {
                binding.progressBar.visibility = View.VISIBLE
                if (currentTab == "rewards") {
                    rewardViewModel.searchReward(searchInput)
                } else {
                    promosViewModel.searchPromo(searchInput)
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        try {
            //FOR REWARDS
            val rewardsList = binding.rewardsList
            rewardsList.layoutManager = LinearLayoutManager(requireContext())
            rewardsList.setHasFixedSize(true)
            rewardList = arrayListOf()
            rewardsList.adapter = rewardsAdapter
            rewardViewModel.fetchRewards()

            //FOR PROMOS
            val promosList = binding.promosList
            promosList.layoutManager = LinearLayoutManager(requireContext())
            promosList.setHasFixedSize(true)
            promoList = arrayListOf()
            promosList.adapter = promosAdapter
            promosViewModel.fetchPromos()
        } catch (error: Exception) {
            Log.e(logTag, error.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}