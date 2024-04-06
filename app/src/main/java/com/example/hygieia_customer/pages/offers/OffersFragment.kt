package com.example.hygieia_customer.pages.offers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.hygieia_customer.R
import com.example.hygieia_customer.databinding.FragmentOffersBinding
import com.example.hygieia_customer.pages.promos.PromosFragment
import com.example.hygieia_customer.pages.promos.PromosViewModel
import com.example.hygieia_customer.pages.rewards.RewardsFragment
import com.example.hygieia_customer.pages.rewards.RewardsViewModel
import com.example.hygieia_customer.utils.Commons
import com.example.hygieia_customer.utils.SharedViewModel

class OffersFragment : Fragment() {

    private var _binding: FragmentOffersBinding? = null
    private val binding get() = _binding!!
    private var commons: Commons = Commons()
    private val rewardViewModel: RewardsViewModel by activityViewModels()
    private val promosViewModel: PromosViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var reward: AppCompatButton
    private lateinit var promo: AppCompatButton
    private var currentTab: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOffersBinding.inflate(inflater, container, false)

        reward = binding.reward
        promo = binding.promo

        commonActions()
        navigation()
        return binding.root
    }

    private fun commonActions() {
        commons.setPageTitle("Offers", binding.root)
        commons.setOnRefreshListener(binding.swipeRefreshLayout) {
            rewardViewModel.fetchRewards()
            promosViewModel.fetchPromos()
        }
    }

    private fun navigation() {
        switchTabsManager(RewardsFragment::class.java)

        sharedViewModel.action.observe(viewLifecycleOwner) { action ->
            currentTab = action
            if (action == "reward") {
                switchTabsManager(RewardsFragment::class.java)
                setColor(reward)
            } else {
                switchTabsManager(PromosFragment::class.java)
                setColor(promo)
            }
        }

        reward.setOnClickListener {
            switchTabsManager(RewardsFragment::class.java)
            setColor(reward)
        }
        promo.setOnClickListener {
            switchTabsManager(PromosFragment::class.java)
            setColor(promo)
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
        val fragmentManager = childFragmentManager
        val transaction = fragmentManager.beginTransaction()

        // Instantiate the fragment using reflection
        val fragment = fragmentClass.getDeclaredConstructor().newInstance() as Fragment

        transaction.replace(R.id.offers_fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}