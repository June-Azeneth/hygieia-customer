package com.example.hygieia_customer.pages.dashboard

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.hygieia_customer.R
import com.example.hygieia_customer.databinding.FragmentDashboardBinding
import com.example.hygieia_customer.model.Ads
import com.example.hygieia_customer.model.Store
import com.example.hygieia_customer.pages.rewards.RewardsViewModel
import com.example.hygieia_customer.pages.scanQR.StoresViewModel
import com.example.hygieia_customer.repository.UserRepo
import com.example.hygieia_customer.utils.Commons
import com.example.hygieia_customer.utils.NetworkViewModel
import com.example.hygieia_customer.utils.SharedViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.concurrent.TimeUnit

class DashboardFragment : Fragment() {
    private var logTag = "DASHBOARD"

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var networkViewModel: NetworkViewModel
    private val userRepo = UserRepo()
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialog: AlertDialog
    private lateinit var actualLayout: ConstraintLayout
    private lateinit var placeholder: ShimmerFrameLayout
    private lateinit var adapter: StoresAdapter
    private val storeList: ArrayList<Store> = arrayListOf()
    private val adsList: ArrayList<Ads> = arrayListOf()
    private val storeViewModel: StoresViewModel by activityViewModels()
    private val adsViewModel: AdsViewModel by activityViewModels()
    private val rewardsViewModel: RewardsViewModel by activityViewModels()
    private var currentIndex = 0
    private lateinit var handler: Handler
    private val delayMillis: Long = TimeUnit.SECONDS.toMillis(5)
    private val carouselRunnable = object : Runnable {
        override fun run() {
            moveCarousel()
            handler.postDelayed(this, delayMillis)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        handler = Handler(Looper.getMainLooper())
        sharedViewModel.setStoreListNav("fromStoreList")
        adsViewModel.fetchAllRewards()
        updateUI()
        initializeVariables()
        setUpNavigation()
        setupRefreshListener()
        setUpRecyclerView()
        startCarousel()
        observeDataChanges()

        return binding.root
    }

    private fun initializeVariables() {
        actualLayout = binding.actualLayout
        placeholder = binding.dashboardPlaceholder
        adapter = StoresAdapter(ArrayList())
    }

    private fun startCarousel() {
        handler.postDelayed(carouselRunnable, delayMillis)
    }


    private fun moveCarousel() {
        if (isAdded && adsList.isNotEmpty()) {
            currentIndex = (currentIndex + 1) % adsList.size
            var currentAd = adsList[currentIndex]
            val imageUrl = adsList[currentIndex].poster

            Glide.with(this)
                .load(imageUrl)
                .into(binding.ads)

            binding.ads.setOnClickListener {
                storeViewModel
                rewardsViewModel.setSelectedReward(adsList[currentIndex].storeId)
                val dialog = AdDetailsDialog(requireContext(), currentAd, findNavController())
                dialog.show()
            }
        }
    }

    private fun observeDataChanges() {
        adsViewModel.adsList.observe(viewLifecycleOwner) { ads ->
            if (ads != null) {
                adsList.clear()
                adsList.addAll(ads)

            }
        }
    }


    private fun setUpNavigation() {
        with(binding) {
            navigateTo(earnMore, R.id.action_dashboardFragment_to_QRScanningFragment)

            rewardsCard.setOnClickListener {
                sharedViewModel.setAction("rewards")
                findNavController().navigate(R.id.action_dashboardFragment_to_offersFragment2)
            }

            promosCard.setOnClickListener {
                sharedViewModel.setAction("promos")
                findNavController().navigate(R.id.action_dashboardFragment_to_offersFragment2)
            }

            binding.text5.setOnClickListener {
                findNavController().navigate(R.id.action_dashboardFragment_to_storeListFragment2)
            }

            binding.announcement.setOnClickListener {
                findNavController().navigate(R.id.action_dashboardFragment_to_announcementFragment2)
            }
        }
    }

    private fun observeNetworkAvailability() {
        networkViewModel = NetworkViewModel(requireContext())
        dialog = MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
            .setView(R.layout.connectivity_dialog_box)
            .setCancelable(true)
            .create()

        networkViewModel.isNetworkAvailable.observe(viewLifecycleOwner) { available ->
            if (available) {
                placeholder.visibility = View.INVISIBLE
                actualLayout.visibility = View.VISIBLE
                dialog.hide()
                updateUI()
            } else {
                placeholder.visibility = View.VISIBLE
                actualLayout.visibility = View.INVISIBLE
                if (!dialog.isShowing)
                    dialog.show()
            }
        }
    }

    private fun setupRefreshListener() {
        Commons().setOnRefreshListener(binding.refreshLayout) {
            observeNetworkAvailability()
        }
        observeNetworkAvailability()
    }

    fun loadUi(load: Boolean) {
        if (load) {
            placeholder.visibility = View.VISIBLE
            actualLayout.visibility = View.GONE
        } else {
            placeholder.visibility = View.GONE
            actualLayout.visibility = View.VISIBLE
        }
    }

    private fun navigateTo(view: View, action: Int) {
        view.setOnClickListener {
            findNavController().navigate(action)
        }
    }

    private fun setUpRecyclerView() {
        try {
            val recyclerView = binding.storeList

            recyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            recyclerView.setHasFixedSize(true)

            recyclerView.adapter = adapter

            storeViewModel.fetchStores { success, error ->
                if (success) {
                    updateUI()
                } else {
                    Commons().showToast("Failed to fetch stores: $error", requireContext())
                }
            }
        } catch (error: Exception) {
            Commons().showToast("An error occurred: $error", requireContext())
        }
    }

    private fun updateUI() {
        try {
            loadUi(true)
            val currentUser = userRepo.getCurrentUserId().toString()
            sharedViewModel.fetchUserDetails(currentUser)
            sharedViewModel.userDetails.observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    loadUi(false)
                    if (user.firstName == "") {
                        binding.username.text = "Hello!"
                    } else {
                        binding.username.text = "Hello ${user.firstName}!"
                    }
                    binding.currentBalance.text = user.currentBalance.toString()
                } else {
                    loadUi(true)
                }
            }

            storeViewModel.storeDetails.observe(viewLifecycleOwner) { stores ->
                if (stores != null) {
                    storeList.clear()
                    storeList.addAll(stores)
                    adapter.setData(storeList)
                }
            }
        } catch (error: Exception) {
            Log.e(logTag, error.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}