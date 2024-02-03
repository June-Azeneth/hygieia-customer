package com.example.hygieia_customer

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.hygieia_customer.databinding.FragmentSignupBinding
import com.example.hygieia_customer.model.UserInfo
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import java.io.ByteArrayOutputStream

class SignupFragment : Fragment() {
//    private val TAG = "LOGINMESSAGES"
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference
    private var _binding: FragmentSignupBinding? = null
    private val binding
        get() = _binding
            ?: error("Binding should not be accessed before onCreateView or after onDestroyView")

    private lateinit var firstName: TextInputEditText
    private lateinit var lastName: TextInputEditText
    private lateinit var address: TextInputEditText
    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private var qrCode: String = ""

    interface UploadCallback {
        fun onUploadSuccess(qrCodeUrl: String)
        fun onUploadFailure()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSignupBinding.inflate(inflater, container, false)

        firstName = binding.firstName
        lastName = binding.lastName
        address = binding.address
        email = binding.email
        password = binding.password

        validEmailChecker()
        validPasswordChecker()

        binding.submit.setOnClickListener {
            submitForm()
//            binding.text.visibility = INVISIBLE
//            binding.progressBar.visibility = VISIBLE
        }

        return binding.root
    }

    private fun submitForm() {
        val validEmail = binding.emailLayout.helperText == null
        val validPassword = binding.emailLayout.helperText == null
        val validFirstName = isNotEmpty(firstName)
        val validLastName = isNotEmpty(lastName)
        val validAddress = isNotEmpty(address)
        val passNotEmpty = isNotEmpty(password)
        val emailNotEmpty = isNotEmpty(email)

        if (validEmail && validPassword && validFirstName && validLastName && validAddress && passNotEmpty && emailNotEmpty) {
            registerUser()
        } else {
            invalidForm()
        }
    }

    private fun registerUser() {
        registerWithEmailAndPassword(email.text.toString(), password.text.toString())
    }

    private fun registerWithEmailAndPassword(email: String, password: String): String? {
        try {
            binding.text.visibility = INVISIBLE
            binding.progressBar.visibility = VISIBLE
            // Create a new user with email and password
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        binding.text.visibility = VISIBLE
                        binding.progressBar.visibility = INVISIBLE
                        // Registration successful, retrieve the UserUID
                        val userUID = getCurrentUserUID()
                        if (userUID != null) {
                            generateQRCode(userUID)
                        }
                        // Perform additional actions or return the UserUID
                    } else {
                        // Registration failed, handle the error
                        // val exception = task.exception
                        // Handle the exception (e.g., show an error message)
                    }
                }
        } catch (e: FirebaseAuthException) {
            e.printStackTrace()
            // Handle exceptions related to FirebaseAuth
        }

        return null // Return null by default, adjust as needed
    }

    private fun getCurrentUserUID(): String? {
        val currentUser: FirebaseUser? = firebaseAuth.currentUser
        return currentUser?.uid
    }

    private fun isNotEmpty(textInputEditText: TextInputEditText): Boolean {
        return textInputEditText.text?.isNotEmpty() == true
    }

    private fun invalidForm() {
        var message = ""
        if (binding.emailLayout.helperText !== null) {
            message += "\n\nEmail: " + binding.emailLayout.helperText
        }
        if (binding.passwordLayout.helperText !== null) {
            message += "\n\nPassword: " + binding.passwordLayout.helperText
        }
        if (binding.firstName.text?.isEmpty() == true || binding.lastName.text?.isEmpty() == true || binding.address.text?.isEmpty() == true
            || binding.email.text?.isEmpty() == true || binding.password.text?.isEmpty() == true
        ) {
            message += "\n\nFill in all required fields"
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Invalid Submission")
            .setMessage(message)
            .setPositiveButton("Okay") { _, _ ->
                run {
                    //DO NOTHING
                }
            }.show()
    }

    private fun validEmailChecker() {
        binding.email.doOnTextChanged { text, _, _, _ ->
            val emailText = text.toString()
            if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                binding.emailLayout.helperText = "Invalid Email Address"
            } else {
                binding.emailLayout.helperText = null
            }
        }
    }

    private fun validPasswordChecker() {
        binding.password.doOnTextChanged { text, _, _, _ ->
            val passwordText = text.toString()
            val helperText: CharSequence? = when {
                passwordText.length < 8 -> "Must be 8 characters"
                !passwordText.matches(".*[A-Z].*".toRegex()) -> "Must have at least 1 Upper-case letter"
                !passwordText.matches(".*[a-z].*".toRegex()) -> "Must contain at least 1 Lower-case letter"
                !passwordText.matches(".*[0-9].*".toRegex()) -> "Must contain at least 1 number"
                !passwordText.matches(".*[@#\$%^&+=].*".toRegex()) -> "Must contain at least 1 special character (@#\$%^&+=)"
                else -> null
            }
            binding.passwordLayout.helperText = helperText
        }
    }

    private fun generateQRCode(userID: String) {
        val writer = QRCodeWriter()
        try {
            val bitMatrix = writer.encode(userID, BarcodeFormat.QR_CODE, 512, 512)
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
            uploadQRCodeImage(bmp, userID, object : UploadCallback {
                override fun onUploadSuccess(qrCodeUrl: String) {
                    // Use qrCodeUrl here
                    qrCode = qrCodeUrl
                    getCurrentUserUID()?.let { saveToFirestore(it) }
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

    private fun uploadQRCodeImage(bmp: Bitmap, userID: String, callback: UploadCallback) {
        // Convert Bitmap to ByteArray
        val byteArrOutputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrOutputStream)
        val data = byteArrOutputStream.toByteArray()

        // Set up Firebase Storage reference
        val imageRef = storageReference.child("qr_codes/$userID.jpg")

        // Upload ByteArray to Firebase Storage
        val uploadTask = imageRef.putBytes(data)

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Image uploaded successfully, get download URL
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    //DO SOMETHING HERE
                    qrCode = uri.toString()
                    Log.e("SIGNUP", uri.toString())

                    val qrCodeUrl = uri.toString()
                    // Call the callback with the URL
                    callback.onUploadSuccess(qrCodeUrl)
                }
            } else {
                // Handle unsuccessful uploads
                Toast.makeText(requireContext(), "Error uploading QR code", Toast.LENGTH_SHORT)
                    .show()

                callback.onUploadFailure()
            }
        }
    }

    private fun saveToFirestore(userID: String) {
        val collectionRef = firestore.collection("consumer")

        val userInfo = UserInfo(
            img_url = qrCode,
            customerName = firstName.text.toString(),
            email = email.text.toString(),
            userLocation = address.text.toString(),
            // Set other fields accordingly
        )

        // Create a data object with the information to be stored
        collectionRef.document(userID)
            .set(userInfo)
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Data uploaded successfully for User with ID: $userID",
                    Toast.LENGTH_SHORT
                ).show()

                binding.text.visibility = VISIBLE
                binding.progressBar.visibility = INVISIBLE
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Error uploading to Firestore: $e",
                    Toast.LENGTH_SHORT
                ).show()

                binding.text.visibility = VISIBLE
                binding.progressBar.visibility = INVISIBLE
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}