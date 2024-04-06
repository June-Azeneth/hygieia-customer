package com.example.hygieia_customer.pages.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.hygieia_customer.R
import com.example.hygieia_customer.model.Store
import com.example.hygieia_customer.pages.scanQR.StoreDiffUtil
import com.google.android.material.imageview.ShapeableImageView

class StoresAdapter(
    private var storeList: ArrayList<Store>,
) :
    RecyclerView.Adapter<StoresAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.store_thumbnail, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = storeList[position]
        Glide.with(holder.itemView)
            .load(currentItem.photo)
            .apply(RequestOptions.centerCropTransform())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.image)
    }

    override fun getItemCount(): Int {
        return storeList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ShapeableImageView = itemView.findViewById(R.id.image)
    }

    fun setData(newStoreList: List<Store>) {
        val oldStoreList = ArrayList(storeList) // Create a copy of the old list
        storeList.clear() // Clear the old list
        storeList.addAll(newStoreList) // Update the list with the new data

        val diffUtil =
            StoreDiffUtil(oldStoreList, storeList) // Pass the old and new lists to the DiffUtil
        val diffResults =
            DiffUtil.calculateDiff(diffUtil) // Calculate the diff between the old and new lists
        diffResults.dispatchUpdatesTo(this) // Dispatch the updates to the adapter
    }
}