package com.example.hygieia_customer.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.hygieia_customer.R
import com.example.hygieia_customer.utils.SharedViewModel
import com.example.hygieia_customer.databinding.FragmentProfileBinding
import com.example.hygieia_customer.model.ProfileOptions
import com.example.hygieia_customer.repository.UserRepo
import com.example.hygieia_customer.utils.Commons
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    val TAG = "ProfileFragmentMessages"
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val userRepo = UserRepo()

    private lateinit var newArrayList: ArrayList<ProfileOptions>
    private lateinit var adapter: ProfileAdapter

    private val optionsIcons = intArrayOf(
        R.drawable.user_avatar,
//        R.drawable.stats,
//        R.drawable.change_pass,
        R.drawable._logout
    )
    private val optionsTitles = arrayOf(
        "Edit Profile",
//        "Stats",
//        "Deactivate Account",
//        "Change Password",
        "Logout"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        newArrayList = ArrayList()
        updateUI()

        for (i in optionsIcons.indices) {
            val icon: Int = optionsIcons[i]
            val title: String = optionsTitles[i]
            val profileOption = ProfileOptions(
                icon,
                title
            )
            newArrayList.add(profileOption)
//            Log.e("TRIAL", newArrayList.toString())
        }

        Commons().setOnRefreshListener(binding.refreshLayout) {
            updateUI()
        }

        setUpOptions()

        return binding.root
    }

    private fun updateUI() {
        try {
            val currentUser = userRepo.getCurrentUserId().toString()
            sharedViewModel.fetchUserDetails(currentUser)

            sharedViewModel.userDetails.observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    binding.customerName.text = requireContext().getString(R.string.customer_name, user.firstName, user.lastName)
                    binding.email.text = user.email

                    val city = user.address?.get("city") ?: ""
                    val province = user.address?.get("province") ?: ""
                    binding.address.text = requireContext().getString(R.string.address_template, city, province)

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
        } catch (error: Exception) {
            Log.e(TAG, error.toString())
        }
    }

    private fun setUpOptions() {
        try {
            val recyclerView = binding.actions
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.setHasFixedSize(true)

            adapter = ProfileAdapter(
                newArrayList,
                object : ProfileAdapter.OnItemClickListener {
                    override fun onItemClick(request: ProfileOptions) {
                        Toast.makeText(requireContext(), request.optionTitle, Toast.LENGTH_SHORT)
                            .show()

                        when (request.optionTitle) {
                            "Edit Profile" -> {
                                findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
                            }

                            "Stats" -> {

                            }

                            "Change Password" -> {

                            }

                            "Logout" -> {
                                Firebase.auth.signOut()
                                requireActivity().finish()
                            }
                        }
                    }
                }
            )
            recyclerView.adapter = adapter
        } catch (error: Exception) {
            Log.e(TAG, error.toString())
        }
    }
}
