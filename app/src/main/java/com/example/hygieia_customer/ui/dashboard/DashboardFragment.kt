package com.example.hygieia_customer.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.hygieia_customer.R
import com.example.hygieia_customer.SharedViewModel
import com.example.hygieia_customer.databinding.FragmentDashboardBinding
import com.example.hygieia_customer.repository.UserRepo
import com.example.hygieia_customer.utils.Commons
import com.google.firebase.auth.FirebaseAuth

class DashboardFragment : Fragment() {
    var TAG = "DASHBOARD"

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val userRepo = UserRepo()
    private lateinit var auth: FirebaseAuth

    // This property is only valid between onCreateView and
    // onDestroyView.
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        //navigation
        navigateTo(binding.earnMore, R.id.action_navigation_dashboard_to_navigation_scanQR)
        navigateTo(
            binding.rewardsCard,
            R.id.action_navigation_dashboard_to_navigation_rewardsFragment
        )
        navigateTo(
            binding.promosCard,
            R.id.action_navigation_dashboard_to_navigation_promosFragment
        )
        navigateTo(binding.profile, R.id.action_navigation_dashboard_to_profileFragment)


        //method calls
        Commons().setOnRefreshListener(binding.refreshLayout) {
            updateUI()
        }
        updateUI()

        return binding.root
    }

    private fun navigateTo(view: View, action: Int) {
        view.setOnClickListener {
            findNavController().navigate(action)
        }
    }

    private fun updateUI() {
        try {
            val currentUser = userRepo.getCurrentUserId().toString()
            sharedViewModel.fetchUserDetails(currentUser)

            sharedViewModel.userDetails.observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    binding.username.text = "Hello ${user.firstName}!"
                    binding.currentBalance.text = user.currentBalance.toString()
                } else {
                    binding.username.text = "Hello Customer!"
                }
            }
        } catch (error: Exception) {
            Log.e(TAG, error.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}