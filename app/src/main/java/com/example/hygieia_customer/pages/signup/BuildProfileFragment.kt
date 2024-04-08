package com.example.hygieia_customer.pages.signup

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.hygieia_customer.LoggedInActivity
import com.example.hygieia_customer.databinding.FragmentSignupBinding
import com.example.hygieia_customer.model.UserInfo
import com.example.hygieia_customer.repository.UserRepo
import com.example.hygieia_customer.utils.Commons
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
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
import java.util.Date

class BuildProfileFragment : Fragment() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference
    private val signUpViewModel: SignUpViewModel by activityViewModels()
    private val userRepo: UserRepo = UserRepo()
    private var _binding: FragmentSignupBinding? = null
    private val binding
        get() = _binding
            ?: error("Binding should not be accessed before onCreateView or after onDestroyView")

    private lateinit var firstName: TextInputEditText
    private lateinit var lastName: TextInputEditText
    private lateinit var sitio: TextInputEditText
    private lateinit var barangay: TextInputEditText
    private lateinit var city: TextInputEditText
    private lateinit var province: TextInputEditText
    private var qrCode: String = ""
    private var emailString: String = ""

    interface UploadCallback {
        fun onUploadSuccess(qrCodeUrl: String)
        fun onUploadFailure()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)

        initializeVariables()
        setUpOnClickListeners()

        return binding.root
    }

    private fun setUpOnClickListeners() {
        binding.submit.setOnClickListener {
            submitForm()
        }
    }

    private fun initializeVariables() {
        firstName = binding.firstName
        lastName = binding.lastName
        sitio = binding.sitio
        barangay = binding.barangay
        city = binding.city
        province = binding.province
    }

    private fun submitForm() {
        val validFirstName = isNotEmpty(firstName)
        val validLastName = isNotEmpty(lastName)
        val validSitio = isNotEmpty(sitio)
        val validBarangay = isNotEmpty(barangay)
        val validCity = isNotEmpty(city)
        val validProvince = isNotEmpty(province)

        if (validFirstName && validLastName && validSitio && validBarangay && validCity && validProvince) {
            signUpViewModel.email.observe(viewLifecycleOwner) {
                generateQRCode(userRepo.getCurrentUserId().toString())
            }
        } else {
            invalidForm()
        }
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
        if (binding.firstName.text?.isEmpty() == true ||
            binding.lastName.text?.isEmpty() == true ||
            binding.sitio.text?.isEmpty() == true ||
            binding.barangay.text?.isEmpty() == true ||
            binding.city.text?.isEmpty() == true ||
            binding.province.text?.isEmpty() == true
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

    private fun generateQRCode(customerId: String) {
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
            uploadQRCodeImage(bmp, customerId, object : UploadCallback {
                override fun onUploadSuccess(qrCodeUrl: String) {
                    qrCode = qrCodeUrl
                    getCurrentUserUID()?.let { saveToFireStore(it) }
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

    private fun uploadQRCodeImage(bmp: Bitmap, customerId: String, callback: UploadCallback) {
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

    private fun saveToFireStore(customerId: String) {
        val collectionRef = fireStore.collection("consumer")

        val addressMap = mapOf(
            "sitio" to sitio.text.toString(),
            "barangay" to barangay.text.toString(),
            "city" to city.text.toString(),
            "province" to province.text.toString()
        )

        val userInfo = getCurrentUserUID()?.let {
            UserInfo(
                firstName = firstName.text.toString(),
                lastName = lastName.text.toString(),
                email = emailString,
                address = addressMap,
                dateRegistered = getCurrentDateTime(),
                qrCode = qrCode,
                id = it
            )
        }

        if (userInfo != null) {
            collectionRef.document(customerId)
                .set(userInfo)
                .addOnSuccessListener {
                    Commons().showToast("Register Success", requireContext())
                    binding.text.visibility = VISIBLE
                    binding.progressBar.visibility = INVISIBLE

                    var intent = Intent(requireContext(), LoggedInActivity::class.java)
                    startActivity(intent)
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}