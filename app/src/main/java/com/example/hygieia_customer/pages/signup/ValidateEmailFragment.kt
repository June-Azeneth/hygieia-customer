package com.example.hygieia_customer.pages.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.hygieia_customer.databinding.FragmentValidateEmailBinding

class ValidateEmailFragment : Fragment() {
    private var _binding: FragmentValidateEmailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentValidateEmailBinding.inflate(inflater, container, false)
        validateEmail()
        return binding.root
    }

    private fun validateEmail() {
        TODO("Not yet implemented")
    }
}