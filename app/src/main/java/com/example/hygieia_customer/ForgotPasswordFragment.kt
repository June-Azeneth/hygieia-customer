package com.example.hygieia_customer

import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.hygieia_customer.databinding.FragmentForgotPasswordBinding
import com.example.hygieia_customer.utils.Commons
import com.example.hygieia_customer.utils.NetworkViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding
        get() = _binding
            ?: error("Binding should not be accessed before onCreateView or after onDestroyView")

    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var auth: FirebaseAuth
    private var commons : Commons = Commons()

    private lateinit var email: TextInputEditText
    private lateinit var submit: AppCompatButton
    private lateinit var dialog: AlertDialog
    private lateinit var successDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        email = binding.email
        submit = binding.submitBTN
        auth = FirebaseAuth.getInstance()

        observeNetworkAvailability()
        return binding.root
    }

    private fun observeNetworkAvailability() {
        networkViewModel = NetworkViewModel(requireContext())
        dialog = MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
            .setView(R.layout.connectivity_dialog_box)
            .setCancelable(true)
            .create()

        networkViewModel.isNetworkAvailable.observe(viewLifecycleOwner) { available ->
            if (available) {
                submit.setOnClickListener {
                    recoverAccount()
                }
            } else {
                if (!dialog.isShowing)
                    dialog.show()
            }
        }
    }

    private fun recoverAccount() {
        val strEmail = email.text.toString()
        if (!TextUtils.isEmpty(strEmail)) {

            auth.sendPasswordResetEmail(strEmail).addOnSuccessListener {
                showMessage()
                findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
            }.addOnFailureListener {
                commons.showToast("An error occurred. Please try again!", requireContext())
            }
        } else {
            commons.showToast("Please provide an email", requireContext())
        }
    }

    private fun showMessage(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Success")
        builder.setMessage("We have sent a recovery link to your email!")
        builder.setPositiveButton("OK") { dialogInterface: DialogInterface, _: Int ->
            // Do something when the "OK" button is clicked
            dialogInterface.dismiss() // Dismiss the dialog
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }
}