package com.example.hygieia_customer.pages.signup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.hygieia_customer.MainActivity
import com.example.hygieia_customer.R
import com.example.hygieia_customer.databinding.FragmentSignUpWithEmailAndPassBinding
import com.example.hygieia_customer.utils.Commons
import com.example.hygieia_customer.utils.NetworkViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class SignUpWithEmailAndPassFragment : Fragment() {
    private var _binding: FragmentSignUpWithEmailAndPassBinding? = null
    private val binding get() = _binding!!
    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var register: ConstraintLayout
    private lateinit var networkViewModel: NetworkViewModel
    private val signUpViewModel: SignUpViewModel by activityViewModels()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpWithEmailAndPassBinding.inflate(layoutInflater, container, false)
        initializeVariables()
        setOnClickListener()
        validPasswordChecker()
        return binding.root
    }

    private fun initializeVariables() {
        email = binding.email
        password = binding.password
        register = binding.register
        passwordLayout = binding.passwordLayout
        networkViewModel = NetworkViewModel(requireContext())
    }

    private fun setOnClickListener() {
        register.setOnClickListener {
            registerUser()
        }

        binding.toLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signUpWithEmailAndPassFragment_to_loginFragment)
        }
    }

    private fun validateFields(callback: (Boolean) -> Unit) {
        if (email.text.toString().isEmpty() || password.text.toString().isEmpty()) {
            Commons().showToast("Fill in all the required fields", requireContext())
            callback(false)
        } else if (!Commons().validateEmail(email.text.toString())) {
            Commons().showToast("Please provide a valid email", requireContext())
            callback(false)
        } else if (passwordLayout.helperText?.isNotEmpty() == true) {
            Commons().showToast(passwordLayout.helperText.toString(), requireContext())
            callback(false)
        } else {
            callback(true)
        }
    }

    private fun registerUser() {
        networkViewModel.isNetworkAvailable.observe(viewLifecycleOwner) { available ->
            if (available) {
                validateFields { valid ->
                    if (valid) {
                        try {
                            val passwordText = password.text.toString()
                            val helperText: CharSequence? = when {
                                passwordText.length < 8 -> "Must be more than 8 characters"
                                !passwordText.matches("(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#\$%^&+=]).{8,}".toRegex()) ->
                                    "Password must contain at least 1 uppercase letter, 1 lowercase letter, 1 digit, and 1 special character"

                                else -> null
                            }

                            if (helperText != null) {
                                Commons().showToast(helperText.toString(), requireContext())
                            } else {
                                binding.text.visibility = View.INVISIBLE
                                binding.progressBar.visibility = View.VISIBLE
                                firebaseAuth.createUserWithEmailAndPassword(
                                    email.text.toString(),
                                    password.text.toString()
                                )
                                    .addOnCompleteListener { task ->
                                        binding.text.visibility = View.VISIBLE
                                        binding.progressBar.visibility = View.INVISIBLE
                                        if (task.isSuccessful) {
                                            sendVerificationEmail()
                                        } else {
                                            Commons().showToast(
                                                "An error occurred. Please try again later.",
                                                requireContext()
                                            )
                                        }
                                    }
                                    .addOnFailureListener { error ->
                                        binding.text.visibility = View.VISIBLE
                                        binding.progressBar.visibility = View.INVISIBLE
                                        val errorMessage = when ((error as? FirebaseAuthException)?.errorCode) {
                                            "ERROR_EMAIL_ALREADY_IN_USE" -> "This email is already in use. Please try a different one."
                                            "ERROR_INVALID_EMAIL" -> "Invalid email format. Please provide a valid email address."
                                            "ERROR_WEAK_PASSWORD" -> "The password is too weak. Please provide a stronger password."
                                            else -> "An error occurred: ${error.message}"
                                        }
                                        Commons().showToast(errorMessage, requireContext())
                                    }
                            }
                        } catch (e: FirebaseAuthException) {
                            e.printStackTrace()
                        }
                    }
                }
            } else {
                Commons().showToast("Network Unavailable", requireContext())
            }
        }
    }


    private fun sendVerificationEmail() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(requireContext(), MainActivity::class.java))
                Commons().showToast("Verification email sent.", requireContext())
            } else {
                Commons().showToast("Failed to send verification email.", requireContext())
            }
        }
    }

    private fun validPasswordChecker() {
        binding.password.doOnTextChanged { text, _, _, _ ->
            val passwordText = text.toString()
            val helperText: CharSequence? = when {
                passwordText.length < 8 -> "Must be more than 8 characters"
                !passwordText.matches("(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#\$%^&+=]).{8,}".toRegex()) ->
                    "Password must contain at least 1 uppercase letter, 1 lowercase letter, 1 digit, and 1 special character"

                else -> null
            }
            passwordLayout.helperText = helperText
        }
    }
}