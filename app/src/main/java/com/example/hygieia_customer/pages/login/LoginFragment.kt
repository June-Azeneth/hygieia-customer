package com.example.hygieia_customer.pages.login

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.hygieia_customer.LoggedInActivity
import com.example.hygieia_customer.R
import com.example.hygieia_customer.databinding.FragmentLoginBinding
import com.example.hygieia_customer.model.UserInfo
import com.example.hygieia_customer.pages.signup.BuildProfileFragment
import com.example.hygieia_customer.repository.UserRepo
import com.example.hygieia_customer.utils.Commons
import com.example.hygieia_customer.utils.NetworkViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import java.io.ByteArrayOutputStream
import java.util.Date

class LoginFragment : Fragment() {
    private var logTag = "LOGINFRAGMENT"
    private lateinit var auth: FirebaseAuth;
    private val userRepo: UserRepo = UserRepo()
    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
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
    private var qrCode: String = ""
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        isUserLoggedId()
        initializeVariables()
        observeNetwork()
        setUpNavigation()

        return binding.root
    }

    private fun isUserLoggedId() {
        auth = Firebase.auth
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val networkViewModel = NetworkViewModel(requireContext())
            networkViewModel.isNetworkAvailable.observe(viewLifecycleOwner) { available ->
                if (available) {
                    userRepo.customerExist { exist ->
                        if (!exist) {
                            findNavController().navigate(R.id.action_loginFragment_to_buildProfile)
                        } else {
                            val intent =
                                Intent( requireView().context, LoggedInActivity::class.java)
                            startActivity(intent)
                            binding.email.text =
                                Editable.Factory.getInstance().newEditable("")
                            binding.password.text =
                                Editable.Factory.getInstance().newEditable("")
                        }
                    }
                } else {
                    if (!dialog.isShowing)
                        dialog.show()
                }
            }
        }
    }

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
                            if (user.isEmailVerified) {
                                userRepo.customerExist { exist ->
                                    val intent =
                                        Intent(
                                            requireView().context,
                                            LoggedInActivity::class.java
                                        )
                                    if (!exist) {
                                        generateQRCode(userRepo.getCurrentUserId().toString()){success->
                                            if(success){
                                                startActivity(intent)
                                            }
                                            else{
                                                Commons().showToast("An error occurred. Please try again later.", requireContext())
                                            }
                                        }
                                    } else {
                                        startActivity(intent)
                                        binding.email.text =
                                            Editable.Factory.getInstance().newEditable("")
                                        binding.password.text =
                                            Editable.Factory.getInstance().newEditable("")
                                    }
                                }
                            } else {
                                Commons().showToast(
                                    "Please verify your email before logging in.",
                                    requireContext()
                                )
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
                                Commons().showToast("Authentication failed.", requireContext())
                            }
                        }
                    }
                }
        } catch (error: Exception) {
            Commons().log(logTag, error.toString())
        }
    }

    private fun generateQRCode(customerId: String, callback : (Boolean) -> Unit) {
        val writer = QRCodeWriter()
        try {
            val bitMatrix = writer.encode(customerId, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(
                        x,
                        y,
                        if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                    )
                }
            }
            uploadQRCodeImage(bmp, customerId, object : BuildProfileFragment.UploadCallback {
                override fun onUploadSuccess(qrCodeUrl: String) {
                    qrCode = qrCodeUrl
                    saveToFireStore(){ success->
                        if(success){
                            callback(true)
                        }
                        else{
                            callback(false)
                        }
                    }
                }

                override fun onUploadFailure() {
                    // Handle failure if needed
                }
            })
        } catch (e: WriterException) {
            e.printStackTrace()
            Toast.makeText(
                requireContext(),
                "Error generating QR code",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun uploadQRCodeImage(
        bmp: Bitmap,
        customerId: String,
        callback: BuildProfileFragment.UploadCallback
    ) {
        val byteArrOutputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrOutputStream)
        val data = byteArrOutputStream.toByteArray()
        val imageRef = storageReference.child("qr_codes/$customerId.jpg")
        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    qrCode = uri.toString()
                    val qrCodeUrl = uri.toString()
                    callback.onUploadSuccess(qrCodeUrl)
                }
            } else {
                Toast.makeText(requireContext(), "Error uploading QR code", Toast.LENGTH_SHORT)
                    .show()
                callback.onUploadFailure()
            }
        }
    }

    private fun getCurrentDateTime(): Timestamp {
        val currentDate = Date()
        return Timestamp(currentDate)
    }

    private fun saveToFireStore(callback: (Boolean) -> Unit) {
        val collectionRef = fireStore.collection("consumer")

        val userInfo = UserInfo(
            email = userRepo.getEmail().toString(),
            dateRegistered = getCurrentDateTime(),
            qrCode = qrCode,
            id = userRepo.getCurrentUserId().toString(),
            status = "active"
        )

        collectionRef.document(userRepo.getCurrentUserId().toString())
            .set(userInfo)
            .addOnSuccessListener {
                var intent = Intent(requireContext(), LoggedInActivity::class.java)
                startActivity(intent)
                callback(true)
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Error uploading to Firestore: $e",
                    Toast.LENGTH_SHORT
                ).show()
                callback(false)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
