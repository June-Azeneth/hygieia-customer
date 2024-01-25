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
import com.example.hygieia_customer.SharedViewModel
import com.example.hygieia_customer.databinding.FragmentEditProfileBinding
import com.example.hygieia_customer.model.UserInfo
//import com.example.hygieia_customer.model.isDifferent
import com.example.hygieia_customer.repository.UserRepo
import com.example.hygieia_customer.ui.profile.profilePhotos.ProfilePhotosFragment
import com.example.hygieia_customer.utils.Commons

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        updateUI()
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
            findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
        }
    }

    private fun updateUI() {
        try {
            sharedViewModel.userDetails.observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    binding.name.setText(user.customerName)
                    binding.email.setText(user.email)
                    binding.address.setText(user.userLocation)
                    selectedProfilePicture = user.userPhoto

//                    selectedProfilePicture = sharedViewModel.selectedProfilePicture.value.toString()

                    Glide.with(requireContext())
                        .load(user.userPhoto)
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
        val currentUserInfo = sharedViewModel.userDetails.value

        val updatedUserInfo = UserInfo(
            img_url = selectedProfilePicture,
            customerName = binding.name.text.toString(),
            email = binding.email.text.toString(),
            userLocation = binding.address.text.toString()
        )

//        Log.d(TAG, "Updated UserInfo:")
//        Log.d(TAG, "  img_url: ${updatedUserInfo.img_url}")
//        Log.d(TAG, "  customerName: ${updatedUserInfo.customerName}")
//        Log.d(TAG, "  email: ${updatedUserInfo.email}")
//        Log.d(TAG, "  userLocation: ${updatedUserInfo.userLocation}")
//
//        Log.d(TAG, "Old UserInfo:")
//        if (currentUserInfo != null) {
//            Log.d(TAG, "  img_url: ${currentUserInfo.userPhoto}")
//            Log.d(TAG, "  customerName: ${currentUserInfo.customerName}")
//            Log.d(TAG, "  email: ${currentUserInfo.email}")
//            Log.d(TAG, "  userLocation: ${currentUserInfo.userLocation}")
//        }


//        if (currentUserInfo != null && updatedUserInfo.isDifferent(currentUserInfo)) {
            // Changes detected, update the profile
            updateUserProfileInRepo(updatedUserInfo)
//        } else {
//            // No changes, show a toast
//            Commons().showToast("No changes detected", requireContext())
//        }
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