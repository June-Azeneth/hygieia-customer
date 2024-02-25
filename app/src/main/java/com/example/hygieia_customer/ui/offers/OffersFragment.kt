package com.example.hygieia_customer.ui.offers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.hygieia_customer.R
import com.example.hygieia_customer.databinding.FragmentOffersBinding
import com.example.hygieia_customer.ui.promos.PromosFragment
import com.example.hygieia_customer.ui.rewards.RewardsFragment
import com.example.hygieia_customer.utils.Commons

class OffersFragment : Fragment() {

    private var _binding: FragmentOffersBinding? = null
    private val binding get() = _binding!!
    private var commons : Commons = Commons()
    private lateinit var reward: AppCompatButton
    private lateinit var promo: AppCompatButton

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

    private fun commonActions(){
        commons.setPageTitle("Offers", binding.root)
    }
    private fun navigation() {
        //Set RewardsTab as default view for frame layout
        switchTabsManager(RewardsFragment::class.java)

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
}