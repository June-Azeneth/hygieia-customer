package com.example.hygieia_customer

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.hygieia_customer.R
import com.example.hygieia_customer.databinding.FragmentLandingPageBinding
import com.example.hygieia_customer.utils.NetworkViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LandingPageFragment : Fragment() {

    private var _binding: FragmentLandingPageBinding? = null
    private val binding get() = _binding ?: error("Binding should not be accessed before onCreateView or after onDestroyView")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLandingPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
            .setView(R.layout.connectivity_dialog_box)
            .setCancelable(true)
            .create()

        val networkViewModel = NetworkViewModel(requireContext())
        networkViewModel.isNetworkAvailable.observe(viewLifecycleOwner) { available ->
            if (available) {   
                binding.getStarted.isEnabled = true
                binding.progressBar.visibility = View.INVISIBLE
                binding.text.visibility = View.VISIBLE
                binding.getStarted.setOnClickListener {
                    findNavController().navigate(R.id.action_landingPageFragment_to_loginFragment)
                }
            } else {
                if(!dialog.isShowing)
                    dialog.show()
                binding.getStarted.isEnabled = false
                binding.progressBar.visibility = View.VISIBLE
                binding.text.visibility = View.INVISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}