package com.example.hygieia_customer

import android.app.AlertDialog
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

class LandingPageFragment : Fragment() {

    private lateinit var dialog : AlertDialog
    private lateinit var progressBar: ProgressBar
    private lateinit var getStartedLayout: RelativeLayout
    private lateinit var getStartedText: TextView
    private var _binding: FragmentLandingPageBinding? = null
    private val binding get() = _binding ?: error("Binding should not be accessed before onCreateView or after onDestroyView")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLandingPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.getStarted.setOnClickListener {
            findNavController().navigate(R.id.action_landingPageFragment_to_loginFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}