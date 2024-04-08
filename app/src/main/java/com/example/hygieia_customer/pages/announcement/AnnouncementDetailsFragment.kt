package com.example.hygieia_customer.pages.announcement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.hygieia_customer.R
import com.example.hygieia_customer.databinding.FragmentAnnouncementDetailsBinding
import com.example.hygieia_customer.utils.Commons
import com.example.hygieia_customer.utils.NetworkManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AnnouncementDetailsFragment : Fragment() {
    private var _binding: FragmentAnnouncementDetailsBinding? = null

    private val binding get() = _binding!!

    private val announcementViewModel: AnnouncementViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnnouncementDetailsBinding.inflate(inflater, container, false)

        observeNetwork()
        setUpUi()

        return binding.root
    }

    private fun setUpUi() {
        Commons().setPageTitle("Details", binding.root)
    }

    private fun observeDataSetChange() {
        announcementViewModel.singleAnnouncement.observe(viewLifecycleOwner) { announcement ->
            if (announcement != null) {
                binding.title.text = announcement.title
                binding.body.text = announcement.body
                binding.date.text = announcement.date.toString()
            }
        }
    }

    private fun observeNetwork() {
        val networkManager = NetworkManager(requireContext())
        networkManager.observe(viewLifecycleOwner) { isNetworkAvailable ->
            if (!isNetworkAvailable) {
                binding.placeholder.visibility = View.VISIBLE
                binding.actualLayout.visibility = View.GONE
                showConnectivityDialog(true)
            } else {
                showConnectivityDialog(false)
                binding.placeholder.visibility = View.GONE
                binding.actualLayout.visibility = View.VISIBLE
                observeDataSetChange()
            }
        }
    }

    private fun showConnectivityDialog(show: Boolean) {
        val dialog =
            MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
                .setView(R.layout.connectivity_dialog_box)
                .setCancelable(true)
                .create()
        if (show) {
            dialog.show()
        } else {
            dialog.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        announcementViewModel.clearAnnouncement()
    }
}