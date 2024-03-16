package com.example.hygieia_customer.pages.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.hygieia_customer.MainActivity
import com.example.hygieia_customer.R
import com.example.hygieia_customer.databinding.FragmentProfileBinding
import com.example.hygieia_customer.model.ProfileOptions
import com.example.hygieia_customer.repository.UserRepo
import com.example.hygieia_customer.utils.Commons
import com.example.hygieia_customer.utils.NetworkViewModel
import com.example.hygieia_customer.utils.SharedViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var networkViewModel: NetworkViewModel
    private val commons: Commons = Commons()
    private val userRepo = UserRepo()

    private lateinit var dialog: AlertDialog
    private lateinit var adapter: ProfileAdapter

    private val optionsIcons = intArrayOf(
        R.drawable.user_avatar,
        R.drawable._logout
    )
    private val optionsTitles = arrayOf(
        "Edit Profile",
        "Logout"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        networkViewModel = NetworkViewModel(requireContext())
        observeNetwork()
        setUpRefreshListener()
        commonActions()

        return binding.root
    }

    private fun commonActions() {
        commons.setPageTitle("Profile", binding.root)
    }

    private fun observeNetwork() {
        dialog = MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
            .setView(R.layout.connectivity_dialog_box)
            .setCancelable(true)
            .create()
        networkViewModel.isNetworkAvailable.observe(viewLifecycleOwner) { available ->
            if (available) {
                binding.profilePlaceholder.visibility = View.INVISIBLE
                binding.actualLayout.visibility = View.VISIBLE
                updateUI()
                setUpOptions()
            } else {
                binding.profilePlaceholder.visibility = View.VISIBLE
                binding.actualLayout.visibility = View.INVISIBLE
                if (!dialog.isShowing)
                    dialog.show()
            }
        }
    }

    private fun setUpRefreshListener() {
        Commons().setOnRefreshListener(binding.refreshLayout) {
            observeNetwork()
        }
    }

    private fun updateUI() {
        val currentUser = userRepo.getCurrentUserId().toString()
        sharedViewModel.fetchUserDetails(currentUser)

        sharedViewModel.userDetails.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.apply {
                    if (user.firstName == "") {
                        customerName.text = "Set Name"
                    } else {
                        customerName.text = requireContext().getString(
                            R.string.customer_name,
                            user.firstName,
                            user.lastName
                        )
                    }
                    email.text = user.email

                    if(user.address.isNullOrEmpty()){
                        address.text = "Set address"
                    }
                    else{
                        val city = user.address?.get("city") ?: ""
                        val province = user.address?.get("province") ?: ""
                        address.text =
                            requireContext().getString(R.string.address_template, city, province)
                    }

                    val photoUrl = user.photo.ifEmpty {
                        R.drawable.user_photo_placeholder
                    }

                    Glide.with(requireContext())
                        .load(photoUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(userPhoto)
                }
            }
        }
    }

    private fun setUpOptions() {
        binding.actions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)

            adapter = ProfileAdapter(
                generateOptions(),
                object : ProfileAdapter.OnItemClickListener {
                    override fun onItemClick(request: ProfileOptions) {
                        when (request.optionTitle) {
                            "Edit Profile" -> findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
                            "Logout" -> {
                                FirebaseAuth.getInstance().signOut()
                                startActivity(Intent(requireContext(), MainActivity::class.java))
                                requireActivity().finish()
                            }
                        }
                    }
                }
            )
        }
    }

    private fun generateOptions(): List<ProfileOptions> {
        return optionsIcons.mapIndexed { index, icon ->
            ProfileOptions(
                icon,
                optionsTitles[index]
            )
        }
    }
}
