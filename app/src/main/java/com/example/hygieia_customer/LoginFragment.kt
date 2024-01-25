package com.example.hygieia_customer

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.hygieia_customer.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginFragment : Fragment() {
    private lateinit var auth: FirebaseAuth;
    private val TAG = "LOGIN MESSAGES"
    private var _binding: FragmentLoginBinding? = null
    private val binding
        get() = _binding
            ?: error("Binding should not be accessed before onCreateView or after onDestroyView")
    private lateinit var intent: Intent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
//        auth = Firebase.auth
        auth = FirebaseAuth.getInstance()

//        updateUI(currentUser)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.forgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
        binding.loginBTN.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val text = binding.textLogin
        val loader = binding.progressBar

        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        try {
            when {
                email.isEmpty() -> {
                    showToast("Please provide an email")
                }
                password.isEmpty() -> {
                    showToast("Please provide a password")
                }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    showToast("Please provide a valid email")
                }
                else -> {
                    text.visibility = INVISIBLE
                    loader.visibility = VISIBLE

                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity()) { task ->
                            text.visibility = VISIBLE
                            loader.visibility = INVISIBLE

                            if (task.isSuccessful) {
                                Log.d(TAG, "signInWithEmail:success")
                                val intent = Intent(requireView().context, LoggedInActivity::class.java)
                                startActivity(intent)
                                binding.email.text = Editable.Factory.getInstance().newEditable("")
                                binding.password.text = Editable.Factory.getInstance().newEditable("")
                            } else {
                                // If sign in fails, handle the specific exception
                                Log.w(TAG, "signInWithEmail:failure", task.exception)

                                // Handle different exception cases
                                when (task.exception) {
                                    is FirebaseAuthInvalidCredentialsException -> {
                                        showToast("Invalid email or password")
                                    }
                                    else -> {
                                        showToast("Authentication failed.")
                                    }
                                }
                            }
                        }
                }
            }
        } catch (error: Exception) {
            Log.e(TAG, error.toString())
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
