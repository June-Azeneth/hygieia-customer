package com.example.hygieia_customer.pages.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.hygieia_customer.LoggedInActivity
import com.example.hygieia_customer.R
import com.example.hygieia_customer.databinding.FragmentLoginBinding
import com.example.hygieia_customer.repository.UserRepo
import com.example.hygieia_customer.utils.Commons
import com.example.hygieia_customer.utils.NetworkViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.auth

class LoginFragment : Fragment() {
    private var logTag = "LOGIN_FRAGMENT"
    private lateinit var auth: FirebaseAuth;
    private val userRepo: UserRepo = UserRepo()
    private var _binding: FragmentLoginBinding? = null
    private val binding
        get() = _binding
            ?: error("Binding should not be accessed before onCreateView or after onDestroyView")
    private lateinit var dialog: AlertDialog
    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private var emailString: String = ""
    private var passwordString: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

//        isUserLoggedId()
        initializeVariables()
        observeNetwork()
        setUpNavigation()

        return binding.root
    }

//    private fun isUserLoggedId() {
//        auth = Firebase.auth
//        val currentUser = auth.currentUser
//        try {
//            userRepo.checkAccountStatus { status ->
//                if (currentUser != null && status == "active") {
//                    val networkViewModel = NetworkViewModel(requireContext())
//                    networkViewModel.isNetworkAvailable.observe(viewLifecycleOwner) { available ->
//                        if (available) {
//                            val intent = Intent(requireView().context, LoggedInActivity::class.java)
//                            startActivity(intent)
//                            binding.email.text = Editable.Factory.getInstance().newEditable("")
//                            binding.password.text = Editable.Factory.getInstance().newEditable("")
//                        } else {
//                            if (!dialog.isShowing)
//                                dialog.show()
//                        }
//                    }
//                }
//            }
//        } catch (error: Exception) {
//            Commons().log("LOGIN", error.message.toString())
//        }
//    }

    private fun initializeVariables() {
        email = binding.email
        password = binding.password
        auth = FirebaseAuth.getInstance()
    }

    private fun setUpNavigation() {
        Commons().setNavigationOnClickListener(
            binding.toRegister,
            R.id.action_loginFragment_to_signUpWithEmailAndPassFragment
        )
        Commons().setNavigationOnClickListener(
            binding.forgotPassword,
            R.id.action_loginFragment_to_forgotPasswordFragment
        )
    }

    private fun observeNetwork() {
        networkViewModel = NetworkViewModel(requireContext())
        dialog = MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
            .setView(R.layout.connectivity_dialog_box)
            .setCancelable(true)
            .create()

        networkViewModel.isNetworkAvailable.observe(viewLifecycleOwner) { available ->
            if (available) {
                binding.loginBTN.setOnClickListener {
                    validateFields()
                }
            } else {
                if (!dialog.isShowing)
                    dialog.show()
            }
        }
    }

    private fun validateFields() {
        emailString = email.text.toString()
        passwordString = password.text.toString()
        if (emailString.isEmpty() || passwordString.isEmpty()) {
            Commons().showToast("Fill in all required fields", requireContext())
        } else if (emailString.isEmpty()) {
            Commons().showToast("Please provide an password", requireContext())
        } else if (passwordString.isEmpty()) {
            Commons().showToast("Please provide an password", requireContext())
        } else if (!Commons().validateEmail(emailString)) {
            Commons().showToast("Please provide a valid email", requireContext())
        } else {
            loginUser()
        }
    }

    private fun loginUser() {
        auth = Firebase.auth
//        val currentUser = auth.currentUser

        val text = binding.textLogin
        val loader = binding.progressBar
        try {
            text.visibility = INVISIBLE
            loader.visibility = VISIBLE

            auth.signInWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(requireActivity()) { task ->
                    text.visibility = VISIBLE
                    loader.visibility = INVISIBLE
                    if (task.isSuccessful) {
                        val user = FirebaseAuth.getInstance().currentUser
                        if (user != null) {
                            userRepo.checkAccountStatus { status ->
                                if (status == "deleted") {
                                    Commons().showToast(
                                        "This account is no longer active",
                                        requireContext()
                                    )
                                } else {
                                    if (user.isEmailVerified) {
                                        userRepo.activateAccount { success ->
                                            if (success) {
                                                val intent = Intent(
                                                    requireView().context,
                                                    LoggedInActivity::class.java
                                                )
                                                startActivity(intent)
                                                requireActivity().finish()
                                            }
                                        }
                                    } else {
                                        Commons().showToast(
                                            "Please verify your email before logging in.",
                                            requireContext()
                                        )
                                        sendVerificationEmail()
                                    }
                                }
                            }
                        }
                    } else {
                        when (task.exception) {
                            is FirebaseAuthInvalidCredentialsException -> {
                                Commons().showToast(
                                    "The credentials you provided does not match any account",
                                    requireContext()
                                )
                            }

                            else -> {
                                Commons().showToast(
                                    "Authentication failed.",
                                    requireContext()
                                )
                            }
                        }
                    }
                }
        } catch (error: Exception) {
            Commons().log(logTag, error.toString())
        }
    }

    private fun sendVerificationEmail() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                FirebaseAuth.getInstance().signOut()
                Commons().showToast("Verification email sent.", requireContext())
            } else {
                Commons().showToast("Failed to send verification email.", requireContext())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
