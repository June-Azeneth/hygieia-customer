package com.example.hygieia_customer.pages.signup

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.hygieia_customer.LoggedInActivity
import com.example.hygieia_customer.MainActivity
import com.example.hygieia_customer.R
import com.example.hygieia_customer.databinding.FragmentSignUpWithEmailAndPassBinding
import com.example.hygieia_customer.model.UserInfo
import com.example.hygieia_customer.repository.UserRepo
import com.example.hygieia_customer.utils.Commons
import com.example.hygieia_customer.utils.NetworkViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import java.io.ByteArrayOutputStream

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpWithEmailAndPassBinding? = null
    private val binding get() = _binding!!
    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var barangay: TextInputEditText
    private lateinit var city: TextInputEditText
    private lateinit var province: TextInputEditText
    private lateinit var firstName: TextInputEditText
    private lateinit var lastName: TextInputEditText
    private lateinit var register: ConstraintLayout
    private lateinit var networkViewModel: NetworkViewModel
    private var qrCode: String = ""
    private var uid: String = ""
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference
    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userRepo: UserRepo = UserRepo()

    //    private val signUpViewModel: SignUpViewModel by activityViewModels()
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
        city = binding.city
        barangay = binding.barangay
        province = binding.province
        firstName = binding.firstName
        lastName = binding.lastName
        networkViewModel = NetworkViewModel(requireContext())
    }

    private fun setOnClickListener() {
        register.setOnClickListener {
            registerUser()
        }

        binding.toLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
    }

    private fun validateFields(callback: (Boolean) -> Unit) {
        if (email.text.toString().isEmpty() || password.text.toString().isEmpty()
            || barangay.text.toString().isEmpty()
            || city.text.toString().isEmpty()
            || province.text.toString().isEmpty()
            || firstName.text.toString().isEmpty()
            || lastName.text.toString().isEmpty()
        ) {
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
            if (!available) {
                Commons().showToast("Network Unavailable", requireContext())
                return@observe
            }

            validateFields { valid ->
                if (!valid) return@validateFields

                val passwordText = password.text.toString()
                val helperText: CharSequence? = when {
                    passwordText.length < 8 -> "Must be more than 8 characters"
                    !passwordText.matches("(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@\$%^&_+=]).{8,}".toRegex()) ->
                        "Password must contain 1 uppercase letter, 1 lowercase letter, 1 digit, and 1 special character (@\$%^&_+=)"
                    else -> null
                }

                if (helperText != null) {
                    Commons().showToast(helperText.toString(), requireContext())
                    return@validateFields
                }

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
                            val user = task.result?.user
                            uid = user?.uid.toString()
                            generateQRCode(uid) { success ->
                                if (success) {
                                    sendVerificationEmail()
                                } else {
                                    Commons().showToast(
                                        "An error occurred. Please try again later.",
                                        requireContext()
                                    )
                                }
                            }
                        } else {
                            val errorMessage =
                                when ((task.exception as? FirebaseAuthException)?.errorCode) {
                                    "ERROR_EMAIL_ALREADY_IN_USE" -> "This email is already in use. Please try a different one."
                                    "ERROR_INVALID_EMAIL" -> "Invalid email format. Please provide a valid email address."
                                    "ERROR_WEAK_PASSWORD" -> "The password is too weak. Please provide a stronger password."
                                    else -> "An error occurred: ${task.exception?.message}"
                                }
                            Commons().showToast(errorMessage, requireContext())
                        }
                    }
                    .addOnFailureListener { error ->
                        binding.text.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.INVISIBLE
                        val errorMessage =
                            when ((error as? FirebaseAuthException)?.errorCode) {
                                "ERROR_EMAIL_ALREADY_IN_USE" -> "This email is already in use. Please try a different one."
                                "ERROR_INVALID_EMAIL" -> "Invalid email format. Please provide a valid email address."
                                "ERROR_WEAK_PASSWORD" -> "The password is too weak. Please provide a stronger password."
                                else -> "An error occurred: ${error.message}"
                            }
                        Commons().showToast(errorMessage, requireContext())
                    }
            }
        }
    }

    private fun generateQRCode(customerId: String, callback: (Boolean) -> Unit) {
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
                    saveToFireStore() { success ->
                        if (success) {
                            callback(true)
                        } else {
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

    private fun saveToFireStore(callback: (Boolean) -> Unit) {
        val collectionRef = fireStore.collection("consumer")

        val addressMap = mapOf(
            "barangay" to barangay.text.toString(),
            "city" to city.text.toString(),
            "province" to province.text.toString()
        )

        val userInfo = UserInfo(
            firstName = firstName.text.toString(),
            lastName = lastName.text.toString(),
            email = email.text.toString(),
            dateRegistered = Commons().getDateAndTime(),
            qrCode = qrCode,
            id = uid,
            address = addressMap,
            status = "unauthenticated"
        )

        collectionRef.document(uid)
            .set(userInfo)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Error uploading to FireStore: $e",
                    Toast.LENGTH_SHORT
                ).show()
                callback(false)
            }
    }

    private fun sendVerificationEmail() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
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
                !passwordText.matches("(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#\$%^&_+=]).{8,}".toRegex()) ->
                    "Password must contain 1 uppercase letter, 1 lowercase letter, 1 digit, and 1 special character (@\$%^&_+=)"
                else -> null
            }
            passwordLayout.helperText = helperText
        }
    }
}