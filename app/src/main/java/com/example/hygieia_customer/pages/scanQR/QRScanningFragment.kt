package com.example.hygieia_customer.pages.scanQR

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.hygieia_customer.R
import com.example.hygieia_customer.utils.SharedViewModel
import com.example.hygieia_customer.databinding.FragmentQRScanningBinding
import com.example.hygieia_customer.repository.UserRepo
import com.example.hygieia_customer.utils.Commons

class QRScanningFragment : Fragment() {
    private var TAG = "SCANQR"
    private val userRepo = UserRepo()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: FragmentQRScanningBinding? = null
    private var commons : Commons = Commons()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQRScanningBinding.inflate(inflater, container, false)
        updateUI()
        commons.setPageTitle("Scan QR Code", binding.root)
        return binding.root
    }

    private fun updateUI() {
        try {
            sharedViewModel.userDetails.observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    binding.currentBalance.text = user.currentBalance.toString()
                    val qrCode = user.qrCode
                    if (!qrCode.isNullOrBlank()) {
                        Glide.with(this)
                            .load(qrCode)
                            .error(R.drawable.image_not_found) // Optional: Image to display if loading fails
                            .into(binding.qrCode)
                    }
                } else {
                    // Handle the case where userDetails is null (user document not found)
                    // You can set default values or show a message to the user
                    binding.currentBalance.text = "0"
                    binding.qrCode.setImageResource(R.drawable.image_not_found)
                }
            }
            val currentUser = userRepo.getCurrentUserId().toString()
            sharedViewModel.fetchUserDetails(currentUser)
        } catch (error: Exception) {
            Log.e(TAG, error.toString())
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}