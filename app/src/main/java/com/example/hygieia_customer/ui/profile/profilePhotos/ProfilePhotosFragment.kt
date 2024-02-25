package com.example.hygieia_customer.ui.profile.profilePhotos

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hygieia_customer.utils.SharedViewModel
import com.example.hygieia_customer.databinding.FragmentProfilePhotosBinding
import com.example.hygieia_customer.model.InAppProfilePhotos
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfilePhotosFragment : DialogFragment() {

    private val tag = "ProfilePicsFragmentMessages"
    private var _binding: FragmentProfilePhotosBinding? = null
    private val binding get() = _binding!!

    private lateinit var pictureList: ArrayList<InAppProfilePhotos>
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageReference: StorageReference =
        storage.reference.child("in_app_profile_photos")

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var cancellationTokenSource: CancellationTokenSource? = null
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilePhotosBinding.inflate(inflater, container, false)
        recyclerView = binding.recyclerView
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pictureList = arrayListOf()
        try {
            cancellationTokenSource = CancellationTokenSource()
            val listAddTask = storageReference.listAll()

            listAddTask.addOnCompleteListener { result ->
                val items: List<StorageReference> = result.result?.items ?: emptyList()
                items.forEachIndexed { _, item ->
                    val downloadUrlTask = item.downloadUrl
                    downloadUrlTask.addOnSuccessListener { uri ->
                        val photoUrl = uri.toString()
                        pictureList.add(InAppProfilePhotos(photoUrl))
                        recyclerView.adapter?.notifyDataSetChanged()
                    }.addOnFailureListener { exception ->
                        Toast.makeText(requireContext(), "$tag $exception", Toast.LENGTH_SHORT)
                            .show()
                    }.addOnCompleteListener {
                        binding.progressBar.visibility = INVISIBLE
                    }
                }
            }

            val recyclerView = binding.recyclerView
            recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
            recyclerView.setHasFixedSize(true)

            val adapter = ProfilePhotosAdapter(
                pictureList,
                object : ProfilePhotosAdapter.OnItemClickListener {
                    override fun onItemClick(picture: InAppProfilePhotos) {
                        sharedViewModel.setSelectedProfilePicture(picture.customerPhoto)
                        dismiss()
                    }
                }
            )

            binding.cancelBTN.setOnClickListener {
                sharedViewModel.setSelectedProfilePicture("")
                cancellationTokenSource?.cancel()
                dismiss()
            }

            recyclerView.adapter = adapter
        } catch (error: Exception) {
            Log.e(tag, error.toString())
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
