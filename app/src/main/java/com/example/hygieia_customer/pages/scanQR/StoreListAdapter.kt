package com.example.hygieia_customer.pages.scanQR

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
import com.google.android.material.imageview.ShapeableImageView

class StoreListAdapter(
    private var storeList: ArrayList<Store>,
    private val onItemClickListener: OnItemClickListener = object : OnItemClickListener {
        override fun onItemClick(item: Store) {
            // Default implementation
        }
    }
) :
    RecyclerView.Adapter<StoreListAdapter.MyViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(item: Store)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.store_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = storeList[position]

        val barangay = currentItem.address?.get("barangay") ?: ""
        val city = currentItem.address?.get("city") ?: ""
        val province = currentItem.address?.get("province") ?: ""
        val formattedAddress = "$barangay, $city, $province"

        holder.address.text = formattedAddress
        Glide.with(holder.itemView)
            .load(currentItem.photo)
            .apply(RequestOptions.centerCropTransform())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.image)

        holder.storeName.text = currentItem.name
        holder.item.setOnClickListener {
            onItemClickListener.onItemClick(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return storeList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ShapeableImageView = itemView.findViewById(R.id.image)
        val address: TextView = itemView.findViewById(R.id.storeAddress)
        val storeName: TextView = itemView.findViewById(R.id.storeName)
        val item: CardView = itemView.findViewById(R.id.item)
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