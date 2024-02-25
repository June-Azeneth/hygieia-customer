package com.example.hygieia_customer.ui.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.hygieia_customer.R
import com.example.hygieia_customer.databinding.FragmentStoreProfileBinding
import com.example.hygieia_customer.ui.promos.PromosViewModel
import com.example.hygieia_customer.ui.rewards.RewardsViewModel
import com.example.hygieia_customer.utils.Commons
import com.facebook.shimmer.ShimmerFrameLayout

class StoreProfileFragment : Fragment() {
    private val rewardViewModel: RewardsViewModel by activityViewModels()
    private val promosViewModel: PromosViewModel by activityViewModels()
    private val storeViewModel: StoreViewModel by activityViewModels()
    private var _binding: FragmentStoreProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var reward: AppCompatButton
    private lateinit var promo: AppCompatButton

    private lateinit var shimmerLayout: ShimmerFrameLayout
    private lateinit var actualLayout: LinearLayout
    private var commons : Commons = Commons()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoreProfileBinding.inflate(inflater, container, false)

        //Initialization
        reward = binding.reward
        promo = binding.promo
        shimmerLayout = binding.shimmerLayout
        actualLayout = binding.actualLayout

        navigation()
        observeViewModel()
        commonActions()

        return binding.root
    }

    private fun loadUI(flag: Boolean) {
        shimmerLayout.startShimmer()
        if (flag) {
            shimmerLayout.visibility = INVISIBLE
            shimmerLayout.stopShimmer()
            actualLayout.visibility = VISIBLE
        } else {
            shimmerLayout.visibility = VISIBLE
            shimmerLayout.startShimmer()
            actualLayout.visibility = INVISIBLE
        }
    }

    private fun navigation() {
        //Set RewardsTab as default view for frame layout
        switchTabsManager(RewardsTab::class.java)

        reward.setOnClickListener {
            switchTabsManager(RewardsTab::class.java)
            setColor(reward)
        }
        promo.setOnClickListener {
            switchTabsManager(PromosTab::class.java)
            setColor(promo)
        }
    }

    private fun setColor(option: AppCompatButton) {
        if (option.id == R.id.reward) {
            option.setBackgroundResource(R.drawable.tab_selector_active)
            promo.setBackgroundResource(R.drawable.tab_selector_inactive)
        } else {
            option.setBackgroundResource(R.drawable.tab_selector_active)
            reward.setBackgroundResource(R.drawable.tab_selector_inactive)
        }
    }

    private fun switchTabsManager(fragmentClass: Class<*>) {
        val fragmentManager = childFragmentManager
        val transaction = fragmentManager.beginTransaction()

        // Instantiate the fragment using reflection
        val fragment = fragmentClass.getDeclaredConstructor().newInstance() as Fragment

        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun commonActions() {
        commons.setOnRefreshListener(binding.refreshLayout) {
            observeViewModel()
        }
        commons.setBackAction(binding.root){
            findNavController().navigate(R.id.action_storeProfileFragment_to_offersFragment)
        }
    }

//    private fun onBackPress(): Boolean {
//        val childFragmentManager = childFragmentManager
//        return if (childFragmentManager.backStackEntryCount > 0) {
//            // If there are entries in the child fragment's back stack, pop it
//            childFragmentManager.popBackStack()
//            true // Back press handled
//        } else {
//            // If there are no more entries in the child fragment's back stack, navigate back
//            requireActivity().onBackPressed()
//            true // Back press handled
//        }
//    }

    private fun observeViewModel() {
        rewardViewModel.selectedReward.observe(viewLifecycleOwner) { storeId ->
            rewardViewModel.fetchRewardsBasedOnStoreId(storeId)
            promosViewModel.fetchPromosBasedOnStoreId(storeId)
            storeViewModel.fetchStoreDetails(storeId)
        }

        storeViewModel.storeDetails.observe(viewLifecycleOwner) { store ->
            if (store != null) {
                binding.storeName.text = store.name
                binding.storeEmail.text = store.email

                val sitio = store.address?.get("sitio") ?: ""
                val barangay = store.address?.get("barangay") ?: ""
                val city = store.address?.get("city") ?: ""
                val province = store.address?.get("province") ?: ""
                binding.storeAddress.text = requireContext().getString(
                    R.string.complete_address_template,
                    sitio,
                    barangay,
                    city,
                    province
                )

                val recyclables = store.recyclables
                val recyclablesContainer = binding.recyclables
                recyclablesContainer.removeAllViews()

                Commons().log("TRIAL", recyclables.toString())

                recyclables?.forEach { recyclable ->
                    val textView = TextView(requireContext()).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            marginEnd = resources.getDimensionPixelSize(R.dimen.tiny)
                        }
                        text = recyclable.toString()
                        setTextColor(ContextCompat.getColor(requireContext(), R.color.text_color))
                        setBackgroundResource(R.drawable.rounded_container)
                        setPadding(
                            resources.getDimensionPixelSize(R.dimen.regular),
                            resources.getDimensionPixelSize(R.dimen.tiniest),
                            resources.getDimensionPixelSize(R.dimen.regular),
                            resources.getDimensionPixelSize(R.dimen.tiniest)
                        )

                    }

                    // Add the TextView to the container
                    recyclablesContainer.addView(textView)
                }


                val photoUrl = store.photo.ifEmpty {
                    R.drawable.store_no_photo_placeholder
                }

                Glide.with(requireContext())
                    .load(photoUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.storePhoto)
                loadUI(true)
            } else {
                loadUI(false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        storeViewModel.clearStoreDetails()
        rewardViewModel.clearRewards()
        _binding = null
    }
}