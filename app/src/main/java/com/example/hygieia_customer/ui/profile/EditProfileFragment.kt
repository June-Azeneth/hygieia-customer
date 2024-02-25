package com.example.hygieia_customer.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.hygieia_customer.R
import com.example.hygieia_customer.utils.SharedViewModel
import com.example.hygieia_customer.databinding.FragmentEditProfileBinding
import com.example.hygieia_customer.model.UserInfo
//import com.example.hygieia_customer.model.isDifferent
import com.example.hygieia_customer.repository.UserRepo
import com.example.hygieia_customer.ui.profile.profilePhotos.ProfilePhotosFragment
import com.example.hygieia_customer.utils.Commons
import com.google.android.material.textfield.TextInputEditText

class EditProfileFragment : Fragment() {
    val TAG = "EditProfileFragmentMessages"
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val userRepo = UserRepo()

    private val profilePicOptionsPopUp: ProfilePhotosFragment by lazy {
        ProfilePhotosFragment()
    }

    private var selectedProfilePicture: String = ""
    private lateinit var firstName: TextInputEditText
    private lateinit var lastName: TextInputEditText
    private lateinit var sitio: TextInputEditText
    private lateinit var barangay: TextInputEditText
    private lateinit var city: TextInputEditText
    private lateinit var province: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        updateUI()
        firstName = binding.firstName
        lastName = binding.lastName
        sitio = binding.sitio
        barangay = binding.barangay
        city = binding.city
        province = binding.province
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.userPhoto.setOnClickListener {
            profilePicOptionsPopUp.show(
                (activity as AppCompatActivity).supportFragmentManager,
                "show Profile Picture Options Pop Up"
            )
            profilePicOptionsPopUp.isCancelable = false
        }

        binding.submitBTN.setOnClickListener {
            updateProfile()
        }

        binding.cancelBTN.setOnClickListener {
            sharedViewModel.setSelectedProfilePicture("")
            findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
        }
    }

    private fun updateUI() {
        try {
            sharedViewModel.userDetails.observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    firstName.setText(user.firstName)
                    lastName.setText(user.lastName)
                    user.address?.let { addressMap ->
                        binding.sitio.setText(addressMap["sitio"])
                        binding.barangay.setText(addressMap["barangay"])
                        binding.city.setText(addressMap["city"])
                        binding.province.setText(addressMap["province"])
                    }

                    //initialize the customer photo here before using it anywhere in the class or else there will be a null input stream error
                    selectedProfilePicture = user.photo

                    val photoUrl = user.photo.ifEmpty {
                        R.drawable.user_photo_placeholder
                    }

                    Glide.with(requireContext())
                        .load(photoUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(binding.userPhoto)
                }
            }

            sharedViewModel.selectedProfilePicture.observe(viewLifecycleOwner) { photo ->
                if (photo != null && photo.isNotEmpty() && photo != selectedProfilePicture) {
                    // Update only if the selected profile picture is not empty and different
                    selectedProfilePicture = photo

                    Glide.with(requireContext())
                        .load(selectedProfilePicture)
                        .apply(RequestOptions.circleCropTransform())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(binding.userPhoto)
                }
            }
        } catch (error: Exception) {
            Log.e(TAG, error.toString())
        }
    }

    private fun updateProfile() {
        val addressMap = mapOf(
            "sitio" to sitio.text.toString(),
            "barangay" to barangay.text.toString(),
            "city" to city.text.toString(),
            "province" to province.text.toString()
        )

        val updatedUserInfo = UserInfo(
            photo = selectedProfilePicture,
            firstName = firstName.text.toString(),
            lastName = lastName.text.toString(),
            address = addressMap
        )
        updateUserProfileInRepo(updatedUserInfo)
    }

    private fun updateUserProfileInRepo(updatedUserInfo: UserInfo) {
        val userId = userRepo.getCurrentUserId()

        if (userId != null) {
            binding.progressBar.visibility = VISIBLE
            userRepo.updateUserProfile(userId, updatedUserInfo) { success ->
                if (success) {
                    binding.progressBar.visibility = INVISIBLE
                    Commons().showToast("Update Profile Success", requireContext())
                    findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)

                } else {
                    binding.progressBar.visibility = INVISIBLE
                    Commons().showToast("Update Profile Failed", requireContext())
                }
            }
        } else {
            Commons().showToast("User unauthenticated", requireContext())
        }
    }
}