package com.example.hygieia_customer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.hygieia_customer.databinding.FragmentLandingPageBinding
import com.example.hygieia_customer.repository.UserRepo
import com.example.hygieia_customer.utils.NetworkViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LandingPageFragment : Fragment() {

    private var _binding: FragmentLandingPageBinding? = null
    private val binding
        get() = _binding
            ?: error("Binding should not be accessed before onCreateView or after onDestroyView")

    private lateinit var auth: FirebaseAuth;
    private val userRepo: UserRepo = UserRepo()
    private lateinit var dialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLandingPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog =
            MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
                .setView(R.layout.connectivity_dialog_box)
                .setCancelable(true)
                .create()

        val networkViewModel = NetworkViewModel(requireContext())
        networkViewModel.isNetworkAvailable.observe(viewLifecycleOwner) { available ->
            if (available) {
                dialog.dismiss()
                showProgressBar(false)
                binding.getStarted.setOnClickListener {
                    findNavController().navigate(R.id.action_landingPageFragment_to_loginFragment)
                }
                isUserLoggedId()
            } else {
                dialog.show()
                showProgressBar(true)
            }
        }
    }

    private fun showProgressBar(show: Boolean) {
        if (show) {
            binding.getStarted.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE
            binding.text.visibility = View.INVISIBLE
        } else {
            binding.getStarted.isEnabled = true
            binding.progressBar.visibility = View.INVISIBLE
            binding.text.visibility = View.VISIBLE
        }
    }

    private fun isUserLoggedId() {
        auth = Firebase.auth
        val currentUser = auth.currentUser
//        userRepo.checkAccountStatus { status ->
//            if (currentUser != null && status == "active") {
//                val intent = Intent(requireView().context, LoggedInActivity::class.java)
//                startActivity(intent)
//                requireActivity().finish()
//            }
//        }

        showProgressBar(true)
        if (currentUser != null) {
            userRepo.checkAccountStatus { status ->
                if (status == "active") {
                    navigateToLoggedInActivity()
                } else {
                    showProgressBar(false)
                }
            }
        } else {
            showProgressBar(false)
        }
    }

    private fun navigateToLoggedInActivity() {
        val intent = Intent(requireView().context, LoggedInActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}