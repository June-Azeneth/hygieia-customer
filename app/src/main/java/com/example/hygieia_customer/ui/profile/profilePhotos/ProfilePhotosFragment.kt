package com.example.hygieia_customer.ui.profile.profilePhotos

import android.app.Dialog
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
import com.example.hygieia_customer.SharedViewModel
import com.example.hygieia_customer.databinding.FragmentProfilePhotosBinding
import com.example.hygieia_customer.model.InAppProfilePhotos
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference

class ProfilePhotosFragment : DialogFragment() {

    private val TAG = "ProfilePicsFragmentMessages"
    private var _binding: FragmentProfilePhotosBinding? = null
    private val binding get() = _binding!!

    private lateinit var pictureList: ArrayList<InAppProfilePhotos>
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageReference: StorageReference =
        storage.reference.child("in_app_profile_photos")

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfilePhotosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pictureList = arrayListOf()
        try {
            val listAddTask: Task<ListResult> = storageReference.listAll()
            listAddTask.addOnCompleteListener { result ->
                val items: List<StorageReference> = result.result?.items ?: emptyList()
                items.forEachIndexed { _, item ->
                    item.downloadUrl.addOnSuccessListener {
                        Log.d("item", it.toString())
                        pictureList.add(InAppProfilePhotos(it.toString()))
                        binding.recyclerView.adapter?.notifyDataSetChanged()
                    }.addOnCompleteListener {
                        binding.progressBar.visibility = INVISIBLE
                    }.addOnFailureListener { exception ->
                        Toast.makeText(requireContext(), "$TAG $exception", Toast.LENGTH_SHORT).show()
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
                        sharedViewModel.setSelectedProfilePicture(picture.img_url)
                        dismiss()
                    }
                }
            )

            binding.cancelBTN.setOnClickListener {
                dismiss()
            }

            recyclerView.adapter = adapter
        } catch (error: Exception) {
            Log.e(TAG, error.toString())
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
