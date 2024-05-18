package com.example.hygieia_customer.pages.store

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.hygieia_customer.R
import com.example.hygieia_customer.databinding.FragmentStoreProfileBinding
import com.example.hygieia_customer.pages.promos.PromosViewModel
import com.example.hygieia_customer.pages.rewards.RewardsViewModel
import com.example.hygieia_customer.utils.Commons
import com.example.hygieia_customer.utils.NetworkViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class StoreProfileFragment : Fragment() {
    private val rewardViewModel: RewardsViewModel by activityViewModels()
    private val promosViewModel: PromosViewModel by activityViewModels()
    private val storeViewModel: StoreViewModel by activityViewModels()
    private lateinit var networkViewModel: NetworkViewModel
    private var _binding: FragmentStoreProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var reward: AppCompatButton
    private lateinit var promo: AppCompatButton
    private lateinit var shimmerLayout: ShimmerFrameLayout
    private lateinit var actualLayout: LinearLayout
    private lateinit var dialog: AlertDialog
    private val commons: Commons = Commons()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoreProfileBinding.inflate(inflater, container, false)

        //method calls
        initializeVariables()
        navigation()
        observeNetwork()
        commonActions()

        return binding.root
    }

    private fun initializeVariables() {
        reward = binding.reward
        promo = binding.promo
        shimmerLayout = binding.shimmerLayout
        actualLayout = binding.actualLayout
        networkViewModel = NetworkViewModel(requireContext())
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
            observeNetwork()
        }
    }

    private fun observeNetwork() {
        dialog = MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
            .setView(R.layout.connectivity_dialog_box)
            .setCancelable(true)
            .create()
        networkViewModel.isNetworkAvailable.observe(viewLifecycleOwner) { available ->
            if (available) {
                observeDataSet()
            } else {
                loadUI(false)
                if (!dialog.isShowing)
                    dialog.show()
            }
        }
    }

    private fun observeDataSet() {
        var fromScreen = storeViewModel.fromScreen.value
        Commons().log("STORE PROFILE", fromScreen.toString())
        if (storeViewModel.fromScreen.value == "offers") {
            rewardViewModel.selectedReward.observe(viewLifecycleOwner) { storeId ->
                rewardViewModel.fetchRewardsBasedOnStoreId(storeId)
                promosViewModel.fetchPromosBasedOnStoreId(storeId)
                storeViewModel.fetchStoreDetails(storeId)
            }
        } else {
            storeViewModel.storeId.observe(viewLifecycleOwner) { id ->
                rewardViewModel.fetchRewardsBasedOnStoreId(id)
                promosViewModel.fetchPromosBasedOnStoreId(id)
                storeViewModel.fetchStoreDetails(id)
            }
        }

        storeViewModel.storeDetails.observe(viewLifecycleOwner) { store ->
            if (store != null) {
                binding.storeName.text = store.name
                binding.storeEmail.text = store.email
                binding.storeAddress.text = store.address

                val recyclables = store.recyclables
                val recyclablesContainer = binding.recyclables
                recyclablesContainer.removeAllViews()

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

                binding.storeAddress.setOnClickListener {
                    openGoogleMaps(store.googleMapLink, requireContext())
                }

                loadUI(true)
            } else {
                loadUI(false)
            }
        }
    }

    private fun openGoogleMaps(link: String, context: Context) {
        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))

        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            Commons().showToast(
                "This action cannot be completed as there is no app available to handle it.",
                context
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        storeViewModel.clearStoreDetails()
        rewardViewModel.clearRewards()
        _binding = null
    }
}